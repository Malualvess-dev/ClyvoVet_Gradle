package br.com.fiap.clyvovet.controller.veterinario;

import br.com.fiap.clyvovet.controller.Mensagens;
import br.com.fiap.clyvovet.dto.request.AtendimentoRequest;
import br.com.fiap.clyvovet.dto.response.ConsultaResponse;
import br.com.fiap.clyvovet.security.UsuarioLogado;
import br.com.fiap.clyvovet.service.AtendimentoService;
import br.com.fiap.clyvovet.service.ConsultaService;
import br.com.fiap.clyvovet.service.VacinaService;
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
@RequestMapping("/veterinario/consultas/{id}/atendimento")
public class AtendimentoController {

    private final ConsultaService consultaService;
    private final AtendimentoService atendimentoService;
    private final VacinaService vacinaService;

    public AtendimentoController(ConsultaService consultaService, AtendimentoService atendimentoService,
                                 VacinaService vacinaService) {
        this.consultaService = consultaService;
        this.atendimentoService = atendimentoService;
        this.vacinaService = vacinaService;
    }

    @GetMapping
    public String formulario(@AuthenticationPrincipal UsuarioLogado usuario, @PathVariable Long id,
                             @ModelAttribute("atendimento") AtendimentoRequest request, Model model) {
        ConsultaResponse consulta = exibirConsulta(usuario, id, model);
        request.setPesoAferidoKg(consulta.pet().pesoKg());
        return "veterinario/atendimento";
    }

    @PostMapping
    public String registrar(@AuthenticationPrincipal UsuarioLogado usuario, @PathVariable Long id,
                            @Valid @ModelAttribute("atendimento") AtendimentoRequest request, BindingResult validacao,
                            Model model, RedirectAttributes redirecionamento) {
        if (validacao.hasErrors()) {
            exibirConsulta(usuario, id, model);
            return "veterinario/atendimento";
        }
        atendimentoService.registrar(usuario.getId(), id, request);
        redirecionamento.addFlashAttribute(Mensagens.SUCESSO, "Atendimento registrado e prontuário atualizado.");
        return "redirect:/veterinario/agenda";
    }

    private ConsultaResponse exibirConsulta(UsuarioLogado usuario, Long consultaId, Model model) {
        ConsultaResponse consulta = consultaService.detalharDoVeterinario(usuario.getId(), consultaId);
        model.addAttribute("consulta", consulta);
        model.addAttribute("historico", consultaService.historicoClinico(consulta.pet().id()));
        model.addAttribute("vacinas", vacinaService.carteiraDoPet(consulta.pet().id()));
        return consulta;
    }
}
