package br.com.fiap.clyvovet.dto.response;

import org.springframework.hateoas.Link;

public record EstadoResponse(
        Integer estadoId,
        String nome,
        String uf,
        Link link
) {
}
