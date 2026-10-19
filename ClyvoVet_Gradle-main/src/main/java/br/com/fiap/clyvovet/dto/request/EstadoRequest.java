package br.com.fiap.clyvovet.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record EstadoRequest(
        @NotBlank
        @Size(max = 50)
        String nome,

        @NotBlank
        @Pattern(
                regexp = "[A-Z]{2}",
                message = "UF deve conter 2 letras maiúsculas"
        )
        String uf
) {
}
