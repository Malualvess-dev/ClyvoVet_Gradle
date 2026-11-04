package br.com.fiap.clyvovet.dto.api;

import br.com.fiap.clyvovet.model.Sexo;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

public record PetApiRequest(
        @NotBlank(message = "Informe o nome do pet.")
        @Size(max = 50, message = "O nome deve ter no máximo 50 caracteres.")
        String nome,

        @NotNull(message = "Selecione a raça.")
        Long racaId,

        @NotNull(message = "Selecione o sexo.")
        Sexo sexo,

        @NotNull(message = "Informe a data de nascimento.")
        @PastOrPresent(message = "A data de nascimento não pode estar no futuro.")
        LocalDate dataNascimento,

        @NotNull(message = "Informe o peso.")
        @DecimalMin(value = "0.01", message = "O peso deve ser de pelo menos 0,01 kg.")
        @DecimalMax(value = "150.0", message = "O peso deve ser de no máximo 150 kg.")
        @Digits(integer = 3, fraction = 2, message = "Use no máximo duas casas decimais no peso.")
        BigDecimal pesoKg,

        @Size(max = 500, message = "As observações devem ter no máximo 500 caracteres.")
        String observacoes
) {
}
