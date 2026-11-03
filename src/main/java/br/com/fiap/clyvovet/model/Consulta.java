package br.com.fiap.clyvovet.model;

import br.com.fiap.clyvovet.exception.RegraNegocioException;
import br.com.fiap.clyvovet.model.conversor.StatusConsultaConverter;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Arrays;

@Entity
@Table(name = "TB_CONSULTA")
public class Consulta {

    static final Duration ANTECEDENCIA_MINIMA_CANCELAMENTO = Duration.ofHours(24);
    private static final int TAMANHO_MINIMO_JUSTIFICATIVA = 5;
    private static final int TAMANHO_MAXIMO_JUSTIFICATIVA = 250;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_CONSULTA")
    @SequenceGenerator(name = "SEQ_CONSULTA", sequenceName = "SEQ_CONSULTA", allocationSize = 1)
    @Column(name = "ID_CONSULTA")
    private Long id;

    @Column(name = "DATA_HORA", nullable = false)
    private LocalDateTime dataHora;

    @Enumerated(EnumType.STRING)
    @Column(name = "MODALIDADE", nullable = false, length = 15)
    private Modalidade modalidade;

    @Convert(converter = StatusConsultaConverter.class)
    @Column(name = "STATUS", nullable = false, length = 15)
    private StatusConsulta status;

    @Column(name = "OBSERVACOES", length = 1000)
    private String motivo;

    @ManyToOne(optional = false)
    @JoinColumn(name = "ID_PET", nullable = false)
    private Pet pet;

    @ManyToOne(optional = false)
    @JoinColumn(name = "ID_VETERINARIO", nullable = false)
    private Veterinario veterinario;

    @Column(name = "JUSTIFICATIVA", length = 250)
    private String justificativa;

    @Column(name = "DATA_SOLICITACAO")
    private LocalDateTime dataSolicitacao;

    @OneToOne(mappedBy = "consulta", cascade = CascadeType.ALL)
    private Prontuario prontuario;

    protected Consulta() {
    }

    private Consulta(Pet pet, Veterinario veterinario, LocalDateTime dataHora, Modalidade modalidade, String motivo,
                     LocalDateTime agora) {
        this.pet = pet;
        this.veterinario = veterinario;
        this.dataHora = dataHora;
        this.modalidade = modalidade;
        this.motivo = motivo.strip();
        this.status = StatusConsulta.SOLICITADA;
        this.dataSolicitacao = agora;
    }

    public static Consulta solicitar(Pet pet, Veterinario veterinario, LocalDateTime dataHora, Modalidade modalidade,
                                     String motivo, LocalDateTime agora) {
        return new Consulta(pet, veterinario, dataHora, modalidade, motivo, agora);
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

    public void garantirQueAguardaAtendimento() {
        exigirStatus(StatusConsulta.CONFIRMADA);
    }

    public void concluir(DadosDoAtendimento dados, LocalDateTime agora) {
        garantirQueAguardaAtendimento();
        if (agora.toLocalDate().isBefore(dataHora.toLocalDate())) {
            throw new RegraNegocioException("O atendimento só pode ser registrado a partir do dia da consulta.");
        }
        prontuario = Prontuario.registrarPara(this, dados);
        status = StatusConsulta.CONCLUIDA;
        pet.atualizarPeso(dados.pesoKg());
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

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    public Modalidade getModalidade() {
        return modalidade;
    }

    public StatusConsulta getStatus() {
        return status;
    }

    public String getMotivo() {
        return motivo;
    }

    public Pet getPet() {
        return pet;
    }

    public Veterinario getVeterinario() {
        return veterinario;
    }

    public String getJustificativa() {
        return justificativa;
    }

    public LocalDateTime getDataSolicitacao() {
        return dataSolicitacao;
    }

    public Prontuario getProntuario() {
        return prontuario;
    }
}
