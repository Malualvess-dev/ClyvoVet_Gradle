package br.com.fiap.clyvovet.dto.request;

import br.com.fiap.clyvovet.model.DadosDoAtendimento;
import br.com.fiap.clyvovet.model.DadosDoAtendimento.DadosDaPrescricao;
import br.com.fiap.clyvovet.model.TipoPrescricao;
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
    @Size(max = 500, message = "O diagnóstico deve ter no máximo 500 caracteres.")
    private String diagnostico;

    @Size(max = 250, message = "Os sintomas devem ter no máximo 250 caracteres.")
    private String sintomas;

    @Size(max = 500, message = "O tratamento deve ter no máximo 500 caracteres.")
    private String tratamento;

    @NotNull(message = "Informe o peso aferido.")
    @DecimalMin(value = "0.01", message = "O peso deve ser de pelo menos 0,01 kg.")
    @DecimalMax(value = "150.0", message = "O peso deve ser de no máximo 150 kg.")
    @Digits(integer = 3, fraction = 2, message = "Use no máximo duas casas decimais.")
    private BigDecimal pesoAferidoKg;

    @DecimalMin(value = "30.0", message = "A temperatura deve ser de pelo menos 30 °C.")
    @DecimalMax(value = "45.0", message = "A temperatura deve ser de no máximo 45 °C.")
    @Digits(integer = 2, fraction = 1, message = "Use no máximo uma casa decimal.")
    private BigDecimal temperatura;

    @Size(max = 250, message = "Os exames devem ter no máximo 250 caracteres.")
    private String examesSolicitados;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate dataRetorno;

    private TipoPrescricao tipoPrescricao;

    @Size(max = 200, message = "A prescrição deve ter no máximo 200 caracteres.")
    private String prescricao;

    @Size(max = 350, message = "A posologia deve ter no máximo 350 caracteres.")
    private String posologia;

    @Size(max = 100, message = "O nome da vacina deve ter no máximo 100 caracteres.")
    private String vacina;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate proximaDose;

    @AssertTrue(message = "Informe o nome da vacina para agendar a próxima dose.")
    public boolean isProximaDoseComVacina() {
        return proximaDose == null || aplicouVacina();
    }

    @AssertTrue(message = "Escolha o tipo da prescrição.")
    public boolean isPrescricaoComTipo() {
        return !possuiPrescricao() || tipoPrescricao != null;
    }

    public boolean aplicouVacina() {
        return vacina != null && !vacina.isBlank();
    }

    private boolean possuiPrescricao() {
        return prescricao != null && !prescricao.isBlank();
    }

    public DadosDoAtendimento dadosDoAtendimento() {
        DadosDaPrescricao dadosDaPrescricao = possuiPrescricao()
                ? new DadosDaPrescricao(tipoPrescricao, prescricao, posologia)
                : null;
        return new DadosDoAtendimento(diagnostico, sintomas, tratamento, pesoAferidoKg, temperatura,
                examesSolicitados, dataRetorno, dadosDaPrescricao);
    }

    public String getDiagnostico() {
        return diagnostico;
    }

    public void setDiagnostico(String diagnostico) {
        this.diagnostico = diagnostico;
    }

    public String getSintomas() {
        return sintomas;
    }

    public void setSintomas(String sintomas) {
        this.sintomas = sintomas;
    }

    public String getTratamento() {
        return tratamento;
    }

    public void setTratamento(String tratamento) {
        this.tratamento = tratamento;
    }

    public BigDecimal getPesoAferidoKg() {
        return pesoAferidoKg;
    }

    public void setPesoAferidoKg(BigDecimal pesoAferidoKg) {
        this.pesoAferidoKg = pesoAferidoKg;
    }

    public BigDecimal getTemperatura() {
        return temperatura;
    }

    public void setTemperatura(BigDecimal temperatura) {
        this.temperatura = temperatura;
    }

    public String getExamesSolicitados() {
        return examesSolicitados;
    }

    public void setExamesSolicitados(String examesSolicitados) {
        this.examesSolicitados = examesSolicitados;
    }

    public LocalDate getDataRetorno() {
        return dataRetorno;
    }

    public void setDataRetorno(LocalDate dataRetorno) {
        this.dataRetorno = dataRetorno;
    }

    public TipoPrescricao getTipoPrescricao() {
        return tipoPrescricao;
    }

    public void setTipoPrescricao(TipoPrescricao tipoPrescricao) {
        this.tipoPrescricao = tipoPrescricao;
    }

    public String getPrescricao() {
        return prescricao;
    }

    public void setPrescricao(String prescricao) {
        this.prescricao = prescricao;
    }

    public String getPosologia() {
        return posologia;
    }

    public void setPosologia(String posologia) {
        this.posologia = posologia;
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
