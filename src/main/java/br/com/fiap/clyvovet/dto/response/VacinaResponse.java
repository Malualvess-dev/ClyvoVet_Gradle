package br.com.fiap.clyvovet.dto.response;

import java.time.LocalDate;

public record VacinaResponse(
        Long id,
        String nome,
        LocalDate dataAplicacao,
        LocalDate proximaDose,
        String petNome
) {
}
