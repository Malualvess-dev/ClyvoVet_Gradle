package br.com.fiap.clyvovet.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(
            JwtAuthenticationFilter jwtAuthenticationFilter
    ) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration configuration
    ) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http
    ) throws Exception {

        return http

                // DESABILITA CSRF
                .csrf(csrf -> csrf.disable())

                // API SEM SESSÃO
                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS
                        )
                )

                // PERMISSÕES
                .authorizeHttpRequests(auth -> auth

                        // =========================
                        // ROTAS PÚBLICAS
                        // =========================

                        // LOGIN
                        .requestMatchers(
                                "/auth/**"
                        ).permitAll()

                        // ERROS DO SPRING
                        .requestMatchers(
                                "/error"
                        ).permitAll()

                        // SWAGGER
                        .requestMatchers(
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/v3/api-docs/**"
                        ).permitAll()

                        // CADASTRO DE USUÁRIO
                        .requestMatchers(
                                HttpMethod.POST,
                                "/usuarios"
                        ).permitAll()


                        // =========================
                        // USUÁRIOS
                        // =========================

                        // LISTAR / BUSCAR USUÁRIOS
                        .requestMatchers(
                                HttpMethod.GET,
                                "/usuarios/**"
                        ).hasRole("VETERINARIO")

                        // ATUALIZAR USUÁRIOS
                        .requestMatchers(
                                HttpMethod.PUT,
                                "/usuarios/**"
                        ).hasRole("VETERINARIO")

                        // EXCLUIR USUÁRIOS
                        .requestMatchers(
                                HttpMethod.DELETE,
                                "/usuarios/**"
                        ).hasRole("VETERINARIO")


                        // =========================
                        // VETERINÁRIOS
                        // =========================

                        .requestMatchers(
                                "/veterinarios/**"
                        ).hasRole("VETERINARIO")


                        // =========================
                        // TUTORES
                        // =========================

                        .requestMatchers(
                                "/tutores/**"
                        ).hasAnyRole(
                                "TUTOR",
                                "VETERINARIO"
                        )


                        // =========================
                        // AGENDAS
                        // =========================

                        .requestMatchers(
                                "/agendas/**"
                        ).hasAnyRole(
                                "TUTOR",
                                "VETERINARIO"
                        )


                        // =========================
                        // ANEXOS
                        // =========================

                        .requestMatchers(
                                "/anexos/**"
                        ).hasAnyRole(
                                "TUTOR",
                                "VETERINARIO"
                        )


                        // =========================
                        // NOTIFICAÇÕES
                        // =========================

                        .requestMatchers(
                                "/notificacoes/**"
                        ).hasAnyRole(
                                "TUTOR",
                                "VETERINARIO"
                        )


                        // =========================
                        // ENDEREÇOS
                        // =========================

                        .requestMatchers(
                                "/enderecos/**"
                        ).hasAnyRole(
                                "TUTOR",
                                "VETERINARIO"
                        )


                        // =========================
                        // ESTADOS
                        // =========================

                        .requestMatchers(
                                "/estados/**"
                        ).hasAnyRole(
                                "TUTOR",
                                "VETERINARIO"
                        )


                        // =========================
                        // CIDADES
                        // =========================

                        .requestMatchers(
                                "/cidades/**"
                        ).hasAnyRole(
                                "TUTOR",
                                "VETERINARIO"
                        )


                        // =========================
                        // BAIRROS
                        // =========================

                        .requestMatchers(
                                "/bairros/**"
                        ).hasAnyRole(
                                "TUTOR",
                                "VETERINARIO"
                        )


                        // QUALQUER OUTRA ROTA
                        .anyRequest().authenticated()
                )

                // FILTRO JWT
                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                )

                .build();
    }
}