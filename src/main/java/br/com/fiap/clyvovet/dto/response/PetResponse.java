package br.com.fiap.clyvovet.dto.response;

import br.com.fiap.clyvovet.model.Especie;

import java.math.BigDecimal;
import java.time.LocalDate;

public record PetResponse(
        Long id,
        String nome,
        Especie especie,
        String raca,
        LocalDate dataNascimento,
        BigDecimal pesoKg
) {
}
