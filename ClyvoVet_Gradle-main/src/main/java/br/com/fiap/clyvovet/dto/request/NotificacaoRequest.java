package br.com.fiap.clyvovet.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public record NotificacaoRequest(
        @NotBlank
        @Size(max = 200)
        String mensagem,

        LocalDateTime dataEnvio,

        @Pattern(
                regexp = "[SN]",
                message = "Lida deve conter S ou N"
        )
        String lida,

        @NotNull
        Integer usuarioId

) {
}
