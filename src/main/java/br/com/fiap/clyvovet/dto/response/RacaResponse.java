package br.com.fiap.clyvovet.dto.response;

public record RacaResponse(
        Long id,
        String nome,
        String especie,
        String porte
) {
}
