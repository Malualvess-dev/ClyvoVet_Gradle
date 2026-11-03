package br.com.fiap.clyvovet.dto.response;

import br.com.fiap.clyvovet.model.Sexo;

import java.math.BigDecimal;
import java.time.LocalDate;

public record PetResponse(
        Long id,
        String nome,
        String especie,
        String raca,
        String porte,
        Sexo sexo,
        LocalDate dataNascimento,
        BigDecimal pesoKg,
        String fotoUrl
) {
}
