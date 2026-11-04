package br.com.fiap.clyvovet.dto.api;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalTime;

public record RemarcacaoApiRequest(
        @NotNull(message = "Selecione a nova data.")
        LocalDate data,

        @NotNull(message = "Selecione o novo horário.")
        LocalTime horario
) {
}
