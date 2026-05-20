package br.com.fiap.clyvovet.dto.response;

import java.time.LocalDateTime;

public record AgendaResponse(
        Integer agendaId,
        String titulo,
        LocalDateTime dataHora,
        String anotacao,
        Integer usuarioId
) {
}
