package br.com.fiap.clyvovet;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import java.time.LocalDateTime;

@TestConfiguration
public class RelogioDeTesteConfig {

    public static final LocalDateTime SEGUNDA_AS_NOVE = LocalDateTime.of(2026, 10, 19, 9, 0);

    @Bean
    @Primary
    public RelogioAjustavel relogioAjustavel() {
        return new RelogioAjustavel(SEGUNDA_AS_NOVE);
    }
}
