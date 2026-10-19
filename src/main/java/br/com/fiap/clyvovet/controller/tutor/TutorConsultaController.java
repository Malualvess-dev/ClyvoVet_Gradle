package br.com.fiap.clyvovet.controller.tutor;

import br.com.fiap.clyvovet.controller.Mensagens;
import br.com.fiap.clyvovet.dto.request.SolicitacaoConsultaRequest;
import br.com.fiap.clyvovet.security.UsuarioLogado;
import br.com.fiap.clyvovet.service.AgendamentoService;
import br.com.fiap.clyvovet.service.ConsultaService;
import br.com.fiap.clyvovet.service.PetService;
import br.com.fiap.clyvovet.service.VeterinarioService;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/tutor/consultas")
public class TutorConsultaController {

    private static final String REDIRECIONAR_PARA_LISTA = "redirect:/tutor/consultas";

    private final ConsultaService consultaService;
    private final AgendamentoService agendamentoService;
    private final PetService petService;
    private final VeterinarioService veterinarioService;

    public TutorConsultaController(ConsultaService consultaService, AgendamentoService agendamentoService,
                                   PetService petService, VeterinarioService veterinarioService) {
        this.consultaService = consultaService;
        this.agendamentoService = agendamentoService;
        this.petService = petService;
        this.veterinarioService = veterinarioService;
    }

    @GetMapping
    public String listar(@AuthenticationPrincipal UsuarioLogado usuario, Model model) {
        model.addAttribute("consultas", consultaService.listarDoTutor(usuario.getId()));
        return "tutor/consultas";
    }

    @GetMapping("/nova")
    public String novaSolicitacao(@AuthenticationPrincipal UsuarioLogado usuario,
                                  @ModelAttribute("solicitacao") SolicitacaoConsultaRequest request, Model model) {
        return exibirFormulario(usuario, request, model);
    }

    @PostMapping
    public String solicitar(@AuthenticationPrincipal UsuarioLogado usuario,
                            @Valid @ModelAttribute("solicitacao") SolicitacaoConsultaRequest request,
                            BindingResult validacao, Model model, RedirectAttributes redirecionamento) {
        if (validacao.hasErrors()) {
            return exibirFormulario(usuario, request, model);
        }
        agendamentoService.solicitar(usuario.getId(), request);
        redirecionamento.addFlashAttribute(Mensagens.SUCESSO,
                "Solicitação enviada! Você será notificado quando o veterinário responder.");
        return REDIRECIONAR_PARA_LISTA;
    }

    @PostMapping("/{id}/cancelamento")
    public String cancelar(@AuthenticationPrincipal UsuarioLogado usuario, @PathVariable Long id,
                           @RequestParam String justificativa, RedirectAttributes redirecionamento) {
        agendamentoService.cancelar(usuario.getId(), id, justificativa);
        redirecionamento.addFlashAttribute(Mensagens.SUCESSO, "Consulta cancelada.");
        return REDIRECIONAR_PARA_LISTA;
    }

    private String exibirFormulario(UsuarioLogado usuario, SolicitacaoConsultaRequest request, Model model) {
        model.addAttribute("pets", petService.listarDoTutor(usuario.getId()));
        model.addAttribute("veterinarios", veterinarioService.listar());
        model.addAttribute("horarios", request.possuiVeterinarioEData()
                ? agendamentoService.horariosDisponiveis(request.getVeterinarioId(), request.getData())
                : List.of());
        return "tutor/nova-consulta";
    }
}
