package br.com.fiap.clyvovet.dto.response;

import org.springframework.hateoas.Link;

import java.time.LocalDateTime;

public record AgendaResponse(
        Integer agendaId,
        String titulo,
        LocalDateTime dataHora,
        String anotacao,
        Integer usuarioId,
        Link link
) {
}
