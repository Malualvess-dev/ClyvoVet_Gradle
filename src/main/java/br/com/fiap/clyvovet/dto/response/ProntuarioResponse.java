package br.com.fiap.clyvovet.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record ProntuarioResponse(
        String diagnostico,
        String sintomas,
        String tratamento,
        BigDecimal pesoKg,
        BigDecimal temperatura,
        String examesSolicitados,
        LocalDate dataRetorno,
        List<PrescricaoResponse> prescricoes
) {
}
