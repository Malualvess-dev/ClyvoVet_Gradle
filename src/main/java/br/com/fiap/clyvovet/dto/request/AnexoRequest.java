package br.com.fiap.clyvovet.dto.request;

import br.com.fiap.clyvovet.model.TipoArquivo;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public record AnexoRequest(
        @NotBlank
        @Size(max = 255)
        String nomeArquivo,

        @NotNull
        TipoArquivo tipoArquivo,

        @Size(max = 255)
        String url,

        @Positive
        Double tamanhoKb,

        LocalDateTime dataCriacao,

        @NotNull
        Integer usuarioId
) {
}
