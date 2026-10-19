package br.com.fiap.clyvovet.web;

import br.com.fiap.clyvovet.security.UsuarioLogado;
import br.com.fiap.clyvovet.service.NotificacaoService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/notificacoes")
public class NotificacaoController {

    private static final String REDIRECIONAR_PARA_LISTA = "redirect:/notificacoes";

    private final NotificacaoService notificacaoService;

    public NotificacaoController(NotificacaoService notificacaoService) {
        this.notificacaoService = notificacaoService;
    }

    @GetMapping
    public String listar(@AuthenticationPrincipal UsuarioLogado usuario, Model model) {
        model.addAttribute("notificacoes", notificacaoService.listar(usuario.getId()));
        return "notificacoes";
    }

    @PostMapping("/{id}/leitura")
    public String marcarComoLida(@AuthenticationPrincipal UsuarioLogado usuario, @PathVariable Long id) {
        notificacaoService.marcarComoLida(usuario.getId(), id);
        return REDIRECIONAR_PARA_LISTA;
    }

    @PostMapping("/leitura")
    public String marcarTodasComoLidas(@AuthenticationPrincipal UsuarioLogado usuario) {
        notificacaoService.marcarTodasComoLidas(usuario.getId());
        return REDIRECIONAR_PARA_LISTA;
    }
}
