package br.com.fiap.clyvovet.dto.api;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

/** Finalização simplificada do aplicativo: a observação clínica vira o diagnóstico do prontuário. */
public record AtendimentoApiRequest(
        @NotBlank(message = "Informe uma observação clínica antes de finalizar.")
        @Size(max = 500, message = "A observação deve ter no máximo 500 caracteres.")
        String observacaoVeterinario,

        @DecimalMin(value = "0.01", message = "O peso deve ser de pelo menos 0,01 kg.")
        @DecimalMax(value = "150.0", message = "O peso deve ser de no máximo 150 kg.")
        BigDecimal pesoKg
) {
}
