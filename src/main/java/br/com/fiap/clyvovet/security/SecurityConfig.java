package br.com.fiap.clyvovet.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.oauth2.client.OAuth2LoginConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    static final String PAGINA_DE_LOGIN = "/login";
    static final String PAGINA_INICIAL = "/painel";

    @Bean
    public SecurityFilterChain filtroDeSeguranca(HttpSecurity http, ProvedoresOAuth2 provedores,
                                                 OAuth2UsuarioService oauth2UsuarioService) throws Exception {
        http
                .authorizeHttpRequests(rotas -> rotas
                        .requestMatchers("/login", "/cadastro", "/css/**", "/img/**", "/error", "/acesso-negado")
                        .permitAll()
                        .requestMatchers("/cadastro/perfil/**").hasRole(UsuarioLogado.PAPEL_PENDENTE)
                        .requestMatchers("/tutor/**").hasRole("TUTOR")
                        .requestMatchers("/veterinario/**").hasRole("VETERINARIO")
                        .anyRequest().authenticated())
                .formLogin(login -> login
                        .loginPage(PAGINA_DE_LOGIN)
                        .usernameParameter("email")
                        .passwordParameter("senha")
                        .defaultSuccessUrl(PAGINA_INICIAL, true)
                        .failureUrl(PAGINA_DE_LOGIN + "?erro"))
                .logout(logout -> logout
                        .logoutSuccessUrl(PAGINA_DE_LOGIN + "?saiu")
                        .deleteCookies("JSESSIONID"))
                .exceptionHandling(erros -> erros.accessDeniedPage("/acesso-negado"));

        if (provedores.algumConfigurado()) {
            http.oauth2Login(oauth2 -> configurarOAuth2(oauth2, provedores, oauth2UsuarioService));
        }
        return http.build();
    }

    private static void configurarOAuth2(OAuth2LoginConfigurer<HttpSecurity> oauth2, ProvedoresOAuth2 provedores,
                                         OAuth2UsuarioService oauth2UsuarioService) {
        oauth2.loginPage(PAGINA_DE_LOGIN)
                .clientRegistrationRepository(provedores.repositorio())
                .userInfoEndpoint(usuario -> usuario.userService(oauth2UsuarioService))
                .defaultSuccessUrl(PAGINA_INICIAL, true)
                .failureUrl(PAGINA_DE_LOGIN + "?erroOAuth");
    }

    @Bean
    public PasswordEncoder codificadorDeSenha() {
        return new BCryptPasswordEncoder();
    }
}
