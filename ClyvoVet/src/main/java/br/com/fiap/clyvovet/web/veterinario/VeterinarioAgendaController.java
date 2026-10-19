package br.com.fiap.clyvovet.web.veterinario;

import br.com.fiap.clyvovet.model.StatusConsulta;
import br.com.fiap.clyvovet.security.UsuarioLogado;
import br.com.fiap.clyvovet.service.AgendamentoService;
import br.com.fiap.clyvovet.service.ConsultaService;
import br.com.fiap.clyvovet.web.Mensagens;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/veterinario")
public class VeterinarioAgendaController {

    private static final String REDIRECIONAR_PARA_SOLICITACOES = "redirect:/veterinario/solicitacoes";

    private final ConsultaService consultaService;
    private final AgendamentoService agendamentoService;

    public VeterinarioAgendaController(ConsultaService consultaService, AgendamentoService agendamentoService) {
        this.consultaService = consultaService;
        this.agendamentoService = agendamentoService;
    }

    @GetMapping("/painel")
    public String painel(@AuthenticationPrincipal UsuarioLogado usuario, Model model) {
        adicionarConsultas(usuario, model, StatusConsulta.SOLICITADA, "solicitacoes");
        adicionarConsultas(usuario, model, StatusConsulta.CONFIRMADA, "agenda");
        return "veterinario/painel";
    }

    @GetMapping("/solicitacoes")
    public String solicitacoes(@AuthenticationPrincipal UsuarioLogado usuario, Model model) {
        adicionarConsultas(usuario, model, StatusConsulta.SOLICITADA, "solicitacoes");
        return "veterinario/solicitacoes";
    }

    @GetMapping("/agenda")
    public String agenda(@AuthenticationPrincipal UsuarioLogado usuario, Model model) {
        adicionarConsultas(usuario, model, StatusConsulta.CONFIRMADA, "agenda");
        return "veterinario/agenda";
    }

    @PostMapping("/consultas/{id}/confirmacao")
    public String confirmar(@AuthenticationPrincipal UsuarioLogado usuario, @PathVariable Long id,
                            RedirectAttributes redirecionamento) {
        agendamentoService.confirmar(usuario.getId(), id);
        redirecionamento.addFlashAttribute(Mensagens.SUCESSO, "Consulta confirmada. O tutor foi notificado.");
        return REDIRECIONAR_PARA_SOLICITACOES;
    }

    @PostMapping("/consultas/{id}/recusa")
    public String recusar(@AuthenticationPrincipal UsuarioLogado usuario, @PathVariable Long id,
                          @RequestParam String justificativa, RedirectAttributes redirecionamento) {
        agendamentoService.recusar(usuario.getId(), id, justificativa);
        redirecionamento.addFlashAttribute(Mensagens.SUCESSO, "Solicitação recusada. O tutor foi notificado.");
        return REDIRECIONAR_PARA_SOLICITACOES;
    }

    private void adicionarConsultas(UsuarioLogado usuario, Model model, StatusConsulta status, String atributo) {
        model.addAttribute(atributo, consultaService.listarDoVeterinarioPorStatus(usuario.getId(), status));
    }
}
