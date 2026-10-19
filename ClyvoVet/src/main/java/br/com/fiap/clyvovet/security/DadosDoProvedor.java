package br.com.fiap.clyvovet.security;

import br.com.fiap.clyvovet.model.ProvedorLogin;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;

import java.util.Map;
import java.util.Optional;

record DadosDoProvedor(String nome, String email, ProvedorLogin provedor) {

    private static final String DOMINIO_SEM_EMAIL_GITHUB = "@users.noreply.github.com";

    static DadosDoProvedor extrair(String registrationId, Map<String, Object> atributos) {
        ProvedorLogin provedor = ProvedorLogin.doRegistro(registrationId);
        return switch (provedor) {
            case GOOGLE -> doGoogle(atributos);
            case GITHUB -> doGithub(atributos);
            case LOCAL -> throw new OAuth2AuthenticationException(new OAuth2Error("provedor_invalido"));
        };
    }

    private static DadosDoProvedor doGoogle(Map<String, Object> atributos) {
        String email = texto(atributos, "email")
                .orElseThrow(() -> new OAuth2AuthenticationException(new OAuth2Error("email_ausente")));
        String nome = texto(atributos, "name").orElse(email);
        return new DadosDoProvedor(nome, email, ProvedorLogin.GOOGLE);
    }

    private static DadosDoProvedor doGithub(Map<String, Object> atributos) {
        String login = texto(atributos, "login").orElseThrow();
        String email = texto(atributos, "email").orElse(login + DOMINIO_SEM_EMAIL_GITHUB);
        String nome = texto(atributos, "name").orElse(login);
        return new DadosDoProvedor(nome, email, ProvedorLogin.GITHUB);
    }

    private static Optional<String> texto(Map<String, Object> atributos, String chave) {
        return Optional.ofNullable(atributos.get(chave))
                .map(Object::toString)
                .filter(valor -> !valor.isBlank());
    }
}
