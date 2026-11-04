package br.com.fiap.clyvovet.dto.api;

import jakarta.validation.constraints.Size;

/** A justificativa é opcional no aplicativo: sem ela, o banco recebe um texto padrão. */
public record CancelamentoApiRequest(
        @Size(max = 250, message = "A justificativa deve ter no máximo 250 caracteres.")
        String justificativa
) {
}
