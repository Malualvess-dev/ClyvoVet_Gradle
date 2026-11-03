package br.com.fiap.clyvovet.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record LeituraColeiraResponse(
        Long id,
        LocalDateTime dataHora,
        BigDecimal batimentoBpm,
        boolean alerta,
        BigDecimal latitude,
        BigDecimal longitude,
        String petNome
) {
}
