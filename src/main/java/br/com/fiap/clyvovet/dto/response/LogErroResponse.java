package br.com.fiap.clyvovet.dto.response;

import java.time.LocalDateTime;

public record LogErroResponse(
        Long id,
        String procedimento,
        String usuario,
        LocalDateTime dataErro,
        long codigo,
        String mensagem
) {
}
