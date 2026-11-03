package br.com.fiap.clyvovet.dto.response;

import java.time.LocalDateTime;

public record LembreteResponse(
        Long id,
        String titulo,
        LocalDateTime dataHora,
        String anotacao
) {
}
