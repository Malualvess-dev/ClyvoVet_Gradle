package br.com.fiap.clyvovet.dto.response;

public record BairroResponse(
        Integer bairroId,
        String nome,
        Integer cidadeId
) {
}
