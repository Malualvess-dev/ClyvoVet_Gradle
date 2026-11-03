package br.com.fiap.clyvovet.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

/** Item da agenda pessoal do usuário (tabela TB_AGENDA do modelo do grupo). */
@Entity
@Table(name = "TB_AGENDA")
public class Lembrete {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_AGENDA")
    @SequenceGenerator(name = "SEQ_AGENDA", sequenceName = "SEQ_AGENDA", allocationSize = 1)
    @Column(name = "ID_AGENDA")
    private Long id;

    @Column(name = "TITULO", nullable = false, length = 85)
    private String titulo;

    @Column(name = "DATA_HORA", nullable = false)
    private LocalDateTime dataHora;

    @Column(name = "ANOTACAO", length = 2500)
    private String anotacao;

    @ManyToOne(optional = false)
    @JoinColumn(name = "ID_USUARIO", nullable = false)
    private Usuario usuario;

    protected Lembrete() {
    }

    public Lembrete(Usuario usuario, String titulo, LocalDateTime dataHora, String anotacao) {
        this.usuario = usuario;
        atualizar(titulo, dataHora, anotacao);
    }

    public final void atualizar(String titulo, LocalDateTime dataHora, String anotacao) {
        this.titulo = titulo.strip();
        this.dataHora = dataHora;
        this.anotacao = Texto.opcional(anotacao);
    }

    public Long getId() {
        return id;
    }

    public String getTitulo() {
        return titulo;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    public String getAnotacao() {
        return anotacao;
    }

    public Usuario getUsuario() {
        return usuario;
    }
}
