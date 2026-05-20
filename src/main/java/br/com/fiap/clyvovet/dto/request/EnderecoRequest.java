package br.com.fiap.clyvovet.dto.request;

import jakarta.validation.constraints.*;

public record EnderecoRequest(

        @NotBlank
        @Size(max = 120)
        String logradouro,

        @Size(max = 10)
        String numero,

        @Size(max = 50)
        String complemento,

        @NotBlank
        @Pattern(
                regexp = "\\d{5}-?\\d{3}",
                message = "O CEP deve estar no formato 00000-000 ou 00000000"
        )
        String cep,

        @DecimalMin(value = "-90.0")
        @DecimalMax(value = "90.0")
        Double latitude,

        @DecimalMin(value = "-180.0")
        @DecimalMax(value = "180.0")
        Double longitude,

        @NotNull
        Integer usuarioId,

        @NotNull
        Integer bairroId
) {
}
