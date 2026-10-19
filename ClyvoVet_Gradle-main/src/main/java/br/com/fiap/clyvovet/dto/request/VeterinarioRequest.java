package br.com.fiap.clyvovet.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record VeterinarioRequest(

        @NotBlank(message = "O nome do veterinário é obrigatório")
        @Size(max = 100)
        String nomeVeterinario,

        @NotBlank(message = "O CRMV é obrigatório")
        @Size(max = 20)
        String crmv,

        @Size(max = 20)
        String telefone,

        @Size(max = 100)
        String especialidade,

        @NotNull(message = "A data de nascimento é obrigatória")
        LocalDate dataNascimento,

        @NotNull(message = "O ID do usuário é obrigatório")
        Integer usuarioId

) {
}
