package br.com.fiap.clyvovet.dto.api;

/**
 * Consulta no formato das telas do aplicativo. O status usa os nomes do app
 * (solicitada, confirmada, recusada, cancelada, realizada).
 */
public record ConsultaApiResponse(
        String id,
        String tutorId,
        String petId,
        String nomePet,
        String tutor,
        String tutorTelefone,
        String veterinarioId,
        String veterinario,
        String dataHora,
        String data,
        String horario,
        String modalidade,
        String motivo,
        String status,
        String justificativa,
        String observacaoVeterinario,
        boolean cancelavel
) {
}
