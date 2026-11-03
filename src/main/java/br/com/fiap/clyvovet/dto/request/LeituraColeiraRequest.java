package br.com.fiap.clyvovet.dto.request;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class LeituraColeiraRequest {

    @NotNull(message = "Selecione o pet.")
    private Long petId;

    @NotNull(message = "Informe os batimentos por minuto.")
    @DecimalMin(value = "10.0", message = "Informe pelo menos 10 bpm.")
    @DecimalMax(value = "400.0", message = "Informe no máximo 400 bpm.")
    @Digits(integer = 3, fraction = 1, message = "Use no máximo uma casa decimal.")
    private BigDecimal batimentoBpm;

    public Long getPetId() {
        return petId;
    }

    public void setPetId(Long petId) {
        this.petId = petId;
    }

    public BigDecimal getBatimentoBpm() {
        return batimentoBpm;
    }

    public void setBatimentoBpm(BigDecimal batimentoBpm) {
        this.batimentoBpm = batimentoBpm;
    }
}
