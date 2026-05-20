package br.com.fiap.clyvovet.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public record AgendaRequest(
        @NotBlank
        @Size(max = 25)
        String titulo,

        @NotNull
        LocalDateTime dataHora,

        @Size(max = 250)
        String anotacao,

        @NotNull
        Integer usuarioId
) {
}
