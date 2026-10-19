package br.com.fiap.clyvovet.dto.response;

public record VeterinarioResponse(
        Long id,
        String nome,
        String crmv,
        String especialidade
) {
}
