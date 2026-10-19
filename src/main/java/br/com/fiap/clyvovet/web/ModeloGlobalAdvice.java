package br.com.fiap.clyvovet.web;

import br.com.fiap.clyvovet.security.UsuarioLogado;
import br.com.fiap.clyvovet.service.NotificacaoService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class ModeloGlobalAdvice {

    private final NotificacaoService notificacaoService;

    public ModeloGlobalAdvice(NotificacaoService notificacaoService) {
        this.notificacaoService = notificacaoService;
    }

    @ModelAttribute("usuarioLogado")
    public UsuarioLogado usuarioLogado(@AuthenticationPrincipal UsuarioLogado usuario) {
        return usuario;
    }

    @ModelAttribute("notificacoesNaoLidas")
    public long notificacoesNaoLidas(@AuthenticationPrincipal UsuarioLogado usuario) {
        return usuario == null ? 0 : notificacaoService.contarNaoLidas(usuario.getId());
    }
}
