package br.com.fiap.clyvovet.model;

import br.com.fiap.clyvovet.exception.RegraNegocioException;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Arrays;

@Entity
@Table(name = "TB_CV_CONSULTA")
public class Consulta {

    static final Duration ANTECEDENCIA_MINIMA_CANCELAMENTO = Duration.ofHours(24);
    private static final int TAMANHO_MINIMO_JUSTIFICATIVA = 5;
    private static final int TAMANHO_MAXIMO_JUSTIFICATIVA = 250;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_CONSULTA")
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "ID_PET", nullable = false)
    private Pet pet;

    @ManyToOne(optional = false)
    @JoinColumn(name = "ID_VETERINARIO", nullable = false)
    private Veterinario veterinario;

    @Column(name = "DATA_HORA", nullable = false)
    private LocalDateTime dataHora;

    @Column(name = "MOTIVO", nullable = false, length = 250)
    private String motivo;

    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS", nullable = false, length = 20)
    private StatusConsulta status;

    @Column(name = "JUSTIFICATIVA", length = 250)
    private String justificativa;

    @Column(name = "DATA_CRIACAO", nullable = false)
    private LocalDateTime dataCriacao;

    @Column(name = "DIAGNOSTICO", length = 1000)
    private String diagnostico;

    @Column(name = "PRESCRICAO", length = 1000)
    private String prescricao;

    @Column(name = "PESO_AFERIDO_KG", precision = 5, scale = 2)
    private BigDecimal pesoAferidoKg;

    @Column(name = "DATA_CONCLUSAO")
    private LocalDateTime dataConclusao;

    protected Consulta() {
    }

    private Consulta(Pet pet, Veterinario veterinario, LocalDateTime dataHora, String motivo, LocalDateTime agora) {
        this.pet = pet;
        this.veterinario = veterinario;
        this.dataHora = dataHora;
        this.motivo = motivo.strip();
        this.status = StatusConsulta.SOLICITADA;
        this.dataCriacao = agora;
    }

    public static Consulta solicitar(Pet pet, Veterinario veterinario, LocalDateTime dataHora,
                                     String motivo, LocalDateTime agora) {
        return new Consulta(pet, veterinario, dataHora, motivo, agora);
    }

    public void confirmar() {
        exigirStatus(StatusConsulta.SOLICITADA);
        status = StatusConsulta.CONFIRMADA;
    }

    public void recusar(String motivoRecusa) {
        exigirStatus(StatusConsulta.SOLICITADA);
        justificativa = validarJustificativa(motivoRecusa);
        status = StatusConsulta.RECUSADA;
    }

    public void cancelar(String motivoCancelamento, LocalDateTime agora) {
        exigirStatus(StatusConsulta.SOLICITADA, StatusConsulta.CONFIRMADA);
        if (status == StatusConsulta.CONFIRMADA && faltaMenosQue(ANTECEDENCIA_MINIMA_CANCELAMENTO, agora)) {
            throw new RegraNegocioException(
                    "Consultas confirmadas só podem ser canceladas com no mínimo 24 horas de antecedência.");
        }
        justificativa = validarJustificativa(motivoCancelamento);
        status = StatusConsulta.CANCELADA;
    }

    public void concluir(Prontuario prontuario, LocalDateTime agora) {
        exigirStatus(StatusConsulta.CONFIRMADA);
        if (agora.toLocalDate().isBefore(dataHora.toLocalDate())) {
            throw new RegraNegocioException("O atendimento só pode ser registrado a partir do dia da consulta.");
        }
        diagnostico = prontuario.diagnostico().strip();
        prescricao = prontuario.prescricao();
        pesoAferidoKg = prontuario.pesoAferidoKg();
        dataConclusao = agora;
        status = StatusConsulta.CONCLUIDA;
        pet.atualizarPeso(pesoAferidoKg);
    }

    public boolean podeSerCancelada(LocalDateTime agora) {
        return status == StatusConsulta.SOLICITADA
                || (status == StatusConsulta.CONFIRMADA && !faltaMenosQue(ANTECEDENCIA_MINIMA_CANCELAMENTO, agora));
    }

    private boolean faltaMenosQue(Duration antecedencia, LocalDateTime agora) {
        return agora.plus(antecedencia).isAfter(dataHora);
    }

    private void exigirStatus(StatusConsulta... permitidos) {
        if (Arrays.stream(permitidos).noneMatch(permitido -> permitido == status)) {
            throw new RegraNegocioException(
                    "Esta ação não é permitida para uma consulta com status \"%s\"."
                            .formatted(status.getDescricao()));
        }
    }

    private static String validarJustificativa(String texto) {
        String justificativa = texto == null ? "" : texto.strip();
        if (justificativa.length() < TAMANHO_MINIMO_JUSTIFICATIVA
                || justificativa.length() > TAMANHO_MAXIMO_JUSTIFICATIVA) {
            throw new RegraNegocioException("Informe uma justificativa entre 5 e 250 caracteres.");
        }
        return justificativa;
    }

    public Long getId() {
        return id;
    }

    public Pet getPet() {
        return pet;
    }

    public Veterinario getVeterinario() {
        return veterinario;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    public String getMotivo() {
        return motivo;
    }

    public StatusConsulta getStatus() {
        return status;
    }

    public String getJustificativa() {
        return justificativa;
    }

    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }

    public String getDiagnostico() {
        return diagnostico;
    }

    public String getPrescricao() {
        return prescricao;
    }

    public BigDecimal getPesoAferidoKg() {
        return pesoAferidoKg;
    }

    public LocalDateTime getDataConclusao() {
        return dataConclusao;
    }
}
