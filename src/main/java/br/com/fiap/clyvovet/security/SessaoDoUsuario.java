package br.com.fiap.clyvovet.security;

import br.com.fiap.clyvovet.model.Usuario;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.stereotype.Component;

@Component
public class SessaoDoUsuario {

    private final SecurityContextRepository repositorioDeContexto = new HttpSessionSecurityContextRepository();

    public void autenticar(Usuario usuario, HttpServletRequest requisicao, HttpServletResponse resposta) {
        UsuarioLogado principal = UsuarioLogado.de(usuario);
        SecurityContext contexto = SecurityContextHolder.createEmptyContext();
        contexto.setAuthentication(UsernamePasswordAuthenticationToken.authenticated(
                principal, null, principal.getAuthorities()));

        if (requisicao.getSession(false) != null) {
            requisicao.changeSessionId();
        }
        SecurityContextHolder.setContext(contexto);
        repositorioDeContexto.saveContext(contexto, requisicao, resposta);
    }
}
