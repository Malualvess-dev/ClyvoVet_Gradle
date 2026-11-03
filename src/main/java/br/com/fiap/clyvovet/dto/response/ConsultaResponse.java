package br.com.fiap.clyvovet.dto.response;

import br.com.fiap.clyvovet.model.Modalidade;
import br.com.fiap.clyvovet.model.StatusConsulta;

import java.time.LocalDateTime;

public record ConsultaResponse(
        Long id,
        LocalDateTime dataHora,
        Modalidade modalidade,
        String motivo,
        StatusConsulta status,
        String justificativa,
        ProntuarioResponse prontuario,
        PetResponse pet,
        String tutorNome,
        String tutorTelefone,
        String veterinarioNome,
        boolean cancelavel
) {
}
