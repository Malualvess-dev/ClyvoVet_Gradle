package br.com.fiap.clyvovet.controller.api;

import br.com.fiap.clyvovet.security.UsuarioLogado;
import org.springframework.security.access.AccessDeniedException;

/** Algumas rotas da API são exclusivas de um perfil (ex.: só o tutor cadastra pets). */
final class AcessoDoAplicativo {

    private AcessoDoAplicativo() {
    }

    static Long tutor(UsuarioLogado usuario) {
        if (!usuario.isTutor()) {
            throw new AccessDeniedException("Esta ação é exclusiva do tutor.");
        }
        return usuario.getId();
    }

    static Long veterinario(UsuarioLogado usuario) {
        if (!usuario.isVeterinario()) {
            throw new AccessDeniedException("Esta ação é exclusiva do veterinário.");
        }
        return usuario.getId();
    }
}
