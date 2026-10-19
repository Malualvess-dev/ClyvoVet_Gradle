package br.com.fiap.clyvovet.dto.response;

import br.com.fiap.clyvovet.model.StatusConsulta;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ConsultaResponse(
        Long id,
        LocalDateTime dataHora,
        String motivo,
        StatusConsulta status,
        String justificativa,
        String diagnostico,
        String prescricao,
        BigDecimal pesoAferidoKg,
        PetResponse pet,
        String tutorNome,
        String tutorTelefone,
        String veterinarioNome,
        boolean cancelavel
) {
}
