package br.com.fiap.clyvovet.dto.api;

import br.com.fiap.clyvovet.model.Modalidade;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.time.LocalTime;

public record ConsultaApiRequest(
        @NotNull(message = "Selecione o pet.")
        Long petId,

        @NotNull(message = "Selecione o veterinário.")
        Long veterinarioId,

        @NotNull(message = "Selecione a data.")
        LocalDate data,

        @NotNull(message = "Selecione um horário disponível.")
        LocalTime horario,

        Modalidade modalidade,

        @NotBlank(message = "Descreva o motivo da consulta.")
        @Size(min = 5, max = 250, message = "O motivo deve ter entre 5 e 250 caracteres.")
        String motivo
) {

    public Modalidade modalidadeOuPadrao() {
        return modalidade == null ? Modalidade.PRESENCIAL : modalidade;
    }
}
