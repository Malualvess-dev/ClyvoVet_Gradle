package br.com.fiap.clyvovet.dto.response;

public record EstadoResponse(
        Integer estadoId,
        String nome,
        String uf
) {
}
