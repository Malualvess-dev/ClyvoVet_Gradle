package br.com.fiap.clyvovet.dto.api;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.br.CPF;

import java.time.LocalDate;

public record CadastroTutorApiRequest(
        @NotBlank(message = "Informe seu nome.")
        @Size(max = 100, message = "O nome deve ter no máximo 100 caracteres.")
        String nome,

        @NotBlank(message = "Informe seu e-mail.")
        @Email(message = "Informe um e-mail válido.")
        @Size(max = 80, message = "O e-mail deve ter no máximo 80 caracteres.")
        String email,

        @NotBlank(message = "Informe uma senha.")
        @Size(min = 8, max = 60, message = "A senha deve ter entre 8 e 60 caracteres.")
        @Pattern(regexp = FormatosDoAplicativo.FORMATO, message = FormatosDoAplicativo.MENSAGEM)
        String senha,

        @NotBlank(message = "Informe seu CPF.")
        @CPF(message = "Informe um CPF válido.")
        String cpf,

        @NotBlank(message = "Informe seu WhatsApp.")
        @Pattern(regexp = FormatosDoAplicativo.TELEFONE, message = FormatosDoAplicativo.MENSAGEM_TELEFONE)
        String whatsapp,

        @NotNull(message = "Informe sua data de nascimento.")
        @Past(message = "A data de nascimento deve estar no passado.")
        LocalDate dataNascimento
) {
}
