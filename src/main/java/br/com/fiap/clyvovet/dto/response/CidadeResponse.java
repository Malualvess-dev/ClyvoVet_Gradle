package br.com.fiap.clyvovet.dto.response;

public record CidadeResponse(
        Integer cidadeId,
        String nome,
        Integer estadoId
) {
}
