package br.com.fiap.clyvovet.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Respostas 401/403 da API em JSON. Não envia o cabeçalho WWW-Authenticate,
 * para o navegador não abrir a janela nativa de login quando o app roda na web.
 */
class RespostasDeSegurancaDaApi implements AuthenticationEntryPoint, AccessDeniedHandler {

    @Override
    public void commence(HttpServletRequest requisicao, HttpServletResponse resposta,
                         AuthenticationException excecao) throws IOException {
        escrever(resposta, HttpServletResponse.SC_UNAUTHORIZED, "E-mail ou senha inválidos.");
    }

    @Override
    public void handle(HttpServletRequest requisicao, HttpServletResponse resposta,
                       AccessDeniedException excecao) throws IOException {
        escrever(resposta, HttpServletResponse.SC_FORBIDDEN, "Seu perfil não tem acesso a esta ação.");
    }

    private static void escrever(HttpServletResponse resposta, int status, String mensagem) throws IOException {
        resposta.setStatus(status);
        resposta.setCharacterEncoding(StandardCharsets.UTF_8.name());
        resposta.setContentType(MediaType.APPLICATION_JSON_VALUE);
        resposta.getWriter().write("{\"status\":%d,\"mensagem\":\"%s\",\"campos\":{}}".formatted(status, mensagem));
    }
}
