package br.com.fiap.clyvovet.web.tutor;

import br.com.fiap.clyvovet.dto.PetForm;
import br.com.fiap.clyvovet.model.Especie;
import br.com.fiap.clyvovet.model.Pet;
import br.com.fiap.clyvovet.security.UsuarioLogado;
import br.com.fiap.clyvovet.service.ConsultaService;
import br.com.fiap.clyvovet.service.PetService;
import br.com.fiap.clyvovet.service.VacinaService;
import br.com.fiap.clyvovet.web.Mensagens;
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

@Controller
@RequestMapping("/tutor/pets")
public class PetController {

    private final PetService petService;
    private final ConsultaService consultaService;
    private final VacinaService vacinaService;

    public PetController(PetService petService, ConsultaService consultaService, VacinaService vacinaService) {
        this.petService = petService;
        this.consultaService = consultaService;
        this.vacinaService = vacinaService;
    }

    @ModelAttribute("especies")
    public Especie[] especies() {
        return Especie.values();
    }

    @GetMapping
    public String listar(@AuthenticationPrincipal UsuarioLogado usuario,
                         @ModelAttribute("pet") PetForm form, Model model) {
        return exibirLista(usuario, model);
    }

    @PostMapping
    public String cadastrar(@AuthenticationPrincipal UsuarioLogado usuario,
                            @Valid @ModelAttribute("pet") PetForm form, BindingResult validacao,
                            Model model, RedirectAttributes redirecionamento) {
        if (validacao.hasErrors()) {
            return exibirLista(usuario, model);
        }
        Pet pet = petService.cadastrar(usuario.getId(), form);
        redirecionamento.addFlashAttribute(Mensagens.SUCESSO, pet.getNome() + " foi cadastrado com sucesso.");
        return "redirect:/tutor/pets";
    }

    @GetMapping("/{id}")
    public String detalhar(@AuthenticationPrincipal UsuarioLogado usuario, @PathVariable Long id, Model model) {
        Pet pet = petService.buscarDoTutor(usuario.getId(), id);
        model.addAttribute("pet", pet);
        model.addAttribute("historico", consultaService.historicoClinico(pet.getId()));
        model.addAttribute("vacinas", vacinaService.carteiraDoPet(pet.getId()));
        return "tutor/pet-detalhe";
    }

    private String exibirLista(UsuarioLogado usuario, Model model) {
        model.addAttribute("pets", petService.listarDoTutor(usuario.getId()));
        return "tutor/pets";
    }
}
