package br.com.fiap.clyvovet.dto.response;

import org.springframework.hateoas.Link;

public record BairroResponse(
        Integer bairroId,
        String nome,
        Integer cidadeId,
        Link link
) {
}
