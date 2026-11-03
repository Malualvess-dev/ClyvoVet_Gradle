package br.com.fiap.clyvovet.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

@Entity
@Table(name = "TB_PRESCRICAO")
public class Prescricao {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_PRESCRICAO")
    @SequenceGenerator(name = "SEQ_PRESCRICAO", sequenceName = "SEQ_PRESCRICAO", allocationSize = 1)
    @Column(name = "ID_PRESCRICAO")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "TIPO", nullable = false, length = 15)
    private TipoPrescricao tipo;

    @Column(name = "DESCRICAO", nullable = false, length = 200)
    private String descricao;

    @Column(name = "POSOLOGIA", length = 350)
    private String posologia;

    @ManyToOne(optional = false)
    @JoinColumn(name = "ID_PRONTUARIO", nullable = false)
    private Prontuario prontuario;

    protected Prescricao() {
    }

    Prescricao(Prontuario prontuario, DadosDoAtendimento.DadosDaPrescricao dados) {
        this.prontuario = prontuario;
        this.tipo = dados.tipo();
        this.descricao = dados.descricao().strip();
        this.posologia = Texto.opcional(dados.posologia());
    }

    public Long getId() {
        return id;
    }

    public TipoPrescricao getTipo() {
        return tipo;
    }

    public String getDescricao() {
        return descricao;
    }

    public String getPosologia() {
        return posologia;
    }

    public Prontuario getProntuario() {
        return prontuario;
    }
}
