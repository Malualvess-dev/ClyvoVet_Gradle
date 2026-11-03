package br.com.fiap.clyvovet.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record AuditoriaColeiraResponse(
        Long id,
        String operacao,
        String usuario,
        LocalDateTime dataHora,
        Long idLeitura,
        BigDecimal batimentoAnterior,
        BigDecimal batimentoNovo
) {
}
