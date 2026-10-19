package br.com.fiap.clyvovet.security;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "clyvovet.oauth2")
public record CredenciaisOAuth2(Cliente google, Cliente github) {

    public record Cliente(String clientId, String clientSecret) {

        boolean configurado() {
            return clientId != null && !clientId.isBlank()
                    && clientSecret != null && !clientSecret.isBlank();
        }
    }
}
