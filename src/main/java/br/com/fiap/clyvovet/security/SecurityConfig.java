package br.com.fiap.clyvovet.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.oauth2.client.OAuth2LoginConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
public class SecurityConfig {

    static final String PAGINA_DE_LOGIN = "/login";
    static final String PAGINA_INICIAL = "/painel";

    /**
     * API REST usada pelo aplicativo mobile: sem sessão nem CSRF, autenticação HTTP Basic
     * (e-mail e senha das mesmas contas do site) e erros em JSON.
     */
    @Bean
    @Order(1)
    public SecurityFilterChain filtroDaApi(HttpSecurity http) throws Exception {
        RespostasDeSegurancaDaApi respostas = new RespostasDeSegurancaDaApi();
        http
                .securityMatcher("/api/**")
                .authorizeHttpRequests(rotas -> rotas
                        .requestMatchers(HttpMethod.OPTIONS, "/api/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/cadastro/**").permitAll()
                        .anyRequest().hasAnyRole("TUTOR", "VETERINARIO"))
                .httpBasic(basic -> basic.authenticationEntryPoint(respostas))
                .csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                .sessionManagement(sessao -> sessao.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(erros -> erros
                        .authenticationEntryPoint(respostas)
                        .accessDeniedHandler(respostas));
        return http.build();
    }

    /** O app no navegador (Expo web) roda em outra origem; no celular o CORS não se aplica. */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuracao = new CorsConfiguration();
        configuracao.setAllowedOriginPatterns(List.of("*"));
        configuracao.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuracao.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        UrlBasedCorsConfigurationSource origem = new UrlBasedCorsConfigurationSource();
        origem.registerCorsConfiguration("/api/**", configuracao);
        return origem;
    }

    @Bean
    @Order(2)
    public SecurityFilterChain filtroDeSeguranca(HttpSecurity http, ProvedoresOAuth2 provedores,
                                                 OAuth2UsuarioService oauth2UsuarioService) throws Exception {
        http
                .authorizeHttpRequests(rotas -> rotas
                        .requestMatchers("/login", "/cadastro", "/css/**", "/img/**", "/error", "/acesso-negado")
                        .permitAll()
                        .requestMatchers("/cadastro/perfil/**").hasRole(UsuarioLogado.PAPEL_PENDENTE)
                        .requestMatchers("/tutor/**").hasRole("TUTOR")
                        .requestMatchers("/veterinario/**").hasRole("VETERINARIO")
                        .requestMatchers("/conta/**").hasAnyRole("TUTOR", "VETERINARIO")
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
