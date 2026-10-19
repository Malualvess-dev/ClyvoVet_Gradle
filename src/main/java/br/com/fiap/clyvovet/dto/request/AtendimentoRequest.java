package br.com.fiap.clyvovet.dto.request;

import br.com.fiap.clyvovet.model.Prontuario;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDate;

public class AtendimentoRequest {

    @NotBlank(message = "Descreva o diagnóstico.")
    @Size(max = 1000, message = "O diagnóstico deve ter no máximo 1000 caracteres.")
    private String diagnostico;

    @Size(max = 1000, message = "A prescrição deve ter no máximo 1000 caracteres.")
    private String prescricao;

    @NotNull(message = "Informe o peso aferido.")
    @DecimalMin(value = "0.1", message = "O peso deve ser de pelo menos 0,1 kg.")
    @DecimalMax(value = "150.0", message = "O peso deve ser de no máximo 150 kg.")
    @Digits(integer = 3, fraction = 2, message = "Use no máximo duas casas decimais.")
    private BigDecimal pesoAferidoKg;

    @Size(max = 80, message = "O nome da vacina deve ter no máximo 80 caracteres.")
    private String vacina;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate proximaDose;

    @AssertTrue(message = "Informe o nome da vacina para agendar a próxima dose.")
    public boolean isProximaDoseComVacina() {
        return proximaDose == null || aplicouVacina();
    }

    public boolean aplicouVacina() {
        return vacina != null && !vacina.isBlank();
    }

    public Prontuario prontuario() {
        return new Prontuario(diagnostico, prescricao, pesoAferidoKg);
    }

    public String getDiagnostico() {
        return diagnostico;
    }

    public void setDiagnostico(String diagnostico) {
        this.diagnostico = diagnostico;
    }

    public String getPrescricao() {
        return prescricao;
    }

    public void setPrescricao(String prescricao) {
        this.prescricao = prescricao;
    }

    public BigDecimal getPesoAferidoKg() {
        return pesoAferidoKg;
    }

    public void setPesoAferidoKg(BigDecimal pesoAferidoKg) {
        this.pesoAferidoKg = pesoAferidoKg;
    }

    public String getVacina() {
        return vacina;
    }

    public void setVacina(String vacina) {
        this.vacina = vacina;
    }

    public LocalDate getProximaDose() {
        return proximaDose;
    }

    public void setProximaDose(LocalDate proximaDose) {
        this.proximaDose = proximaDose;
    }
}
