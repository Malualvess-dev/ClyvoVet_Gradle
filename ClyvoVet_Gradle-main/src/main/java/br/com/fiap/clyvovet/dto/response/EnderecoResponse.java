package br.com.fiap.clyvovet.dto.response;

import org.springframework.hateoas.Link;

public record EnderecoResponse(
        Integer enderecoId,
        String logradouro,
        String numero,
        String complemento,
        String cep,
        Double latitude,
        Double longitude,
        Integer usuarioId,
        Integer bairroId,
        Link link
) {
}
