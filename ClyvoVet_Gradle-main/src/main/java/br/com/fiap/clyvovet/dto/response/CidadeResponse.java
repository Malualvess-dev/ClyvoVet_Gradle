package br.com.fiap.clyvovet.dto.response;

import org.springframework.hateoas.Link;

public record CidadeResponse(
        Integer cidadeId,
        String nome,
        Integer estadoId,
        Link link
) {
}
