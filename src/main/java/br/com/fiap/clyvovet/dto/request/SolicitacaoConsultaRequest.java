package br.com.fiap.clyvovet.dto.request;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class SolicitacaoConsultaRequest {

    @NotNull(message = "Selecione o pet.")
    private Long petId;

    @NotNull(message = "Selecione o veterinário.")
    private Long veterinarioId;

    @NotNull(message = "Selecione a data.")
    @FutureOrPresent(message = "A data não pode estar no passado.")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate data;

    @NotNull(message = "Selecione um horário disponível.")
    @DateTimeFormat(pattern = "HH:mm")
    private LocalTime horario;

    @NotBlank(message = "Descreva o motivo da consulta.")
    @Size(min = 5, max = 250, message = "O motivo deve ter entre 5 e 250 caracteres.")
    private String motivo;

    public boolean possuiVeterinarioEData() {
        return veterinarioId != null && data != null;
    }

    public LocalDateTime dataHora() {
        return LocalDateTime.of(data, horario);
    }

    public Long getPetId() {
        return petId;
    }

    public void setPetId(Long petId) {
        this.petId = petId;
    }

    public Long getVeterinarioId() {
        return veterinarioId;
    }

    public void setVeterinarioId(Long veterinarioId) {
        this.veterinarioId = veterinarioId;
    }

    public LocalDate getData() {
        return data;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }

    public LocalTime getHorario() {
        return horario;
    }

    public void setHorario(LocalTime horario) {
        this.horario = horario;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }
}
