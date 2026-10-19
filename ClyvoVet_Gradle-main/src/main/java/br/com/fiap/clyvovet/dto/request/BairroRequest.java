package br.com.fiap.clyvovet.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record BairroRequest(
        @NotBlank
        @Size(max = 100)
        String nome,

        @NotNull
        Integer cidadeId
) {
}
