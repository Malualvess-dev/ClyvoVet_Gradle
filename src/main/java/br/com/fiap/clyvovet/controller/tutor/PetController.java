package br.com.fiap.clyvovet.controller.tutor;

import br.com.fiap.clyvovet.controller.Mensagens;
import br.com.fiap.clyvovet.dto.request.PetRequest;
import br.com.fiap.clyvovet.dto.response.PetResponse;
import br.com.fiap.clyvovet.dto.response.RacaResponse;
import br.com.fiap.clyvovet.model.Sexo;
import br.com.fiap.clyvovet.security.UsuarioLogado;
import br.com.fiap.clyvovet.service.ColeiraService;
import br.com.fiap.clyvovet.service.ConsultaService;
import br.com.fiap.clyvovet.service.HistoricoSaudeService;
import br.com.fiap.clyvovet.service.PetService;
import br.com.fiap.clyvovet.service.banco.ProcedimentosDoBanco;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/tutor/pets")
public class PetController {

    private final PetService petService;
    private final ConsultaService consultaService;
    private final HistoricoSaudeService historicoSaudeService;
    private final ColeiraService coleiraService;
    private final ProcedimentosDoBanco procedimentosDoBanco;

    public PetController(PetService petService, ConsultaService consultaService,
                         HistoricoSaudeService historicoSaudeService, ColeiraService coleiraService,
                         ProcedimentosDoBanco procedimentosDoBanco) {
        this.petService = petService;
        this.consultaService = consultaService;
        this.historicoSaudeService = historicoSaudeService;
        this.coleiraService = coleiraService;
        this.procedimentosDoBanco = procedimentosDoBanco;
    }

    /** Raças agrupadas por espécie, para o select com optgroup do formulário. */
    @ModelAttribute("racasPorEspecie")
    public Map<String, List<RacaResponse>> racasPorEspecie() {
        return petService.listarRacas().stream()
                .collect(Collectors.groupingBy(RacaResponse::especie, LinkedHashMap::new, Collectors.toList()));
    }

    @ModelAttribute("sexos")
    public Sexo[] sexos() {
        return Sexo.values();
    }

    @GetMapping
    public String listar(@AuthenticationPrincipal UsuarioLogado usuario,
                         @ModelAttribute("pet") PetRequest request, Model model) {
        return exibirLista(usuario, model);
    }

    @PostMapping
    public String cadastrar(@AuthenticationPrincipal UsuarioLogado usuario,
                            @Valid @ModelAttribute("pet") PetRequest request, BindingResult validacao,
                            Model model, RedirectAttributes redirecionamento) {
        if (validacao.hasErrors()) {
            return exibirLista(usuario, model);
        }
        PetResponse pet = petService.cadastrar(usuario.getId(), request);
        redirecionamento.addFlashAttribute(Mensagens.SUCESSO, pet.nome() + " foi cadastrado com sucesso.");
        return "redirect:/tutor/pets";
    }

    @GetMapping("/{id}")
    public String detalhar(@AuthenticationPrincipal UsuarioLogado usuario, @PathVariable Long id, Model model) {
        PetResponse pet = petService.detalharDoTutor(usuario.getId(), id);
        model.addAttribute("pet", pet);
        model.addAttribute("idade", procedimentosDoBanco.idadeDoPet(pet.dataNascimento()));
        model.addAttribute("historico", consultaService.historicoClinico(pet.id()));
        model.addAttribute("historicoSaude", historicoSaudeService.doPet(pet.id()));
        model.addAttribute("leituras", coleiraService.ultimasLeituras(pet.id()));
        return "tutor/pet-detalhe";
    }

    private String exibirLista(UsuarioLogado usuario, Model model) {
        model.addAttribute("pets", petService.listarDoTutor(usuario.getId()));
        return "tutor/pets";
    }
}
