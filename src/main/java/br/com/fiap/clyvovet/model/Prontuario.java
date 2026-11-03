package br.com.fiap.clyvovet.model;

import br.com.fiap.clyvovet.exception.RegraNegocioException;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.OrderBy;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "TB_PRONTUARIO")
public class Prontuario {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_PRONTUARIO")
    @SequenceGenerator(name = "SEQ_PRONTUARIO", sequenceName = "SEQ_PRONTUARIO", allocationSize = 1)
    @Column(name = "ID_PRONTUARIO")
    private Long id;

    @OneToOne(optional = false)
    @JoinColumn(name = "ID_CONSULTA", nullable = false, unique = true)
    private Consulta consulta;

    @Column(name = "DIAGNOSTICO", nullable = false, length = 500)
    private String diagnostico;

    @Column(name = "TRATAMENTO", length = 500)
    private String tratamento;

    @Column(name = "SINTOMAS", length = 250)
    private String sintomas;

    @Column(name = "PESO_ANIMAL", precision = 5, scale = 2)
    private BigDecimal pesoAnimal;

    @Column(name = "TEMPERATURA", precision = 4, scale = 1)
    private BigDecimal temperatura;

    @Column(name = "EXAMES_SOLICITADOS", length = 250)
    private String examesSolicitados;

    @Column(name = "DATA_RETORNO")
    private LocalDate dataRetorno;

    @OneToMany(mappedBy = "prontuario", cascade = CascadeType.ALL)
    @OrderBy("id")
    private List<Prescricao> prescricoes = new ArrayList<>();

    protected Prontuario() {
    }

    private Prontuario(Consulta consulta, DadosDoAtendimento dados) {
        this.consulta = consulta;
        this.diagnostico = dados.diagnostico().strip();
        this.sintomas = Texto.opcional(dados.sintomas());
        this.tratamento = Texto.opcional(dados.tratamento());
        this.pesoAnimal = dados.pesoKg();
        this.temperatura = dados.temperatura();
        this.examesSolicitados = Texto.opcional(dados.examesSolicitados());
        this.dataRetorno = dados.dataRetorno();
        if (dados.prescricao() != null) {
            prescricoes.add(new Prescricao(this, dados.prescricao()));
        }
    }

    static Prontuario registrarPara(Consulta consulta, DadosDoAtendimento dados) {
        LocalDate diaDaConsulta = consulta.getDataHora().toLocalDate();
        if (dados.dataRetorno() != null && !dados.dataRetorno().isAfter(diaDaConsulta)) {
            throw new RegraNegocioException("A data de retorno deve ser posterior ao dia da consulta.");
        }
        return new Prontuario(consulta, dados);
    }

    public Long getId() {
        return id;
    }

    public Consulta getConsulta() {
        return consulta;
    }

    public String getDiagnostico() {
        return diagnostico;
    }

    public String getTratamento() {
        return tratamento;
    }

    public String getSintomas() {
        return sintomas;
    }

    public BigDecimal getPesoAnimal() {
        return pesoAnimal;
    }

    public BigDecimal getTemperatura() {
        return temperatura;
    }

    public String getExamesSolicitados() {
        return examesSolicitados;
    }

    public LocalDate getDataRetorno() {
        return dataRetorno;
    }

    public List<Prescricao> getPrescricoes() {
        return List.copyOf(prescricoes);
    }
}
