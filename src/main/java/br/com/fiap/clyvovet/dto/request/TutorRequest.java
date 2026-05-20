package br.com.fiap.clyvovet.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record TutorRequest(
        @NotBlank
        @Size(max = 100)
        String nomeTutor,

        @NotBlank
        @Pattern(
                regexp = "\\d{3}\\.?\\d{3}\\.?\\d{3}-?\\d{2}",
                message = "CPF deve estar no formato 000.000.000-00 ou apenas números"
        )
        String cpf,

        @Pattern(
                regexp = "^\\(?\\d{2}\\)?\\s?\\d{4,5}-?\\d{4}$",
                message = "Telefone inválido"
        )
        String telefone,

        LocalDate dataNascimento,

        @NotNull
        Integer usuarioId
) {
}
