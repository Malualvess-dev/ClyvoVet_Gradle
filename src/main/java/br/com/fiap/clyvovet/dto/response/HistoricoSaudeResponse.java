package br.com.fiap.clyvovet.dto.response;

import java.time.LocalDate;

public record HistoricoSaudeResponse(
        Long id,
        String evento,
        LocalDate dataEvento,
        String detalhes,
        LocalDate proximaDose,
        String petNome
) {
}
