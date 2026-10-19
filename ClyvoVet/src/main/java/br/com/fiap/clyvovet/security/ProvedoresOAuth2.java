package br.com.fiap.clyvovet.security;

import org.springframework.security.config.oauth2.client.CommonOAuth2Provider;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Component
public class ProvedoresOAuth2 {

    private final List<ClientRegistration> registros;

    public ProvedoresOAuth2(CredenciaisOAuth2 credenciais) {
        this.registros = Stream.of(
                        registrar(CommonOAuth2Provider.GOOGLE, "google", credenciais.google(), "profile", "email"),
                        registrar(CommonOAuth2Provider.GITHUB, "github", credenciais.github(), "read:user", "user:email"))
                .flatMap(Optional::stream)
                .toList();
    }

    private static Optional<ClientRegistration> registrar(CommonOAuth2Provider provedor, String id,
                                                          CredenciaisOAuth2.Cliente cliente, String... escopos) {
        if (cliente == null || !cliente.configurado()) {
            return Optional.empty();
        }
        return Optional.of(provedor.getBuilder(id)
                .clientId(cliente.clientId())
                .clientSecret(cliente.clientSecret())
                .scope(escopos)
                .build());
    }

    public boolean algumConfigurado() {
        return !registros.isEmpty();
    }

    public List<String> idsConfigurados() {
        return registros.stream().map(ClientRegistration::getRegistrationId).toList();
    }

    public ClientRegistrationRepository repositorio() {
        return new InMemoryClientRegistrationRepository(registros);
    }
}
