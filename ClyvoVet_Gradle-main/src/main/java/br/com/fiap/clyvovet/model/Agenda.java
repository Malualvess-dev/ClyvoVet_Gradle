package br.com.fiap.clyvovet.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "TB_AGENDA")
public class Agenda {
    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "agenda_sequence"
    )
    @SequenceGenerator(
            name = "agenda_sequence",
            sequenceName = "AGENDA_SEQUENCE", allocationSize = 1
    )

    @Column(name = "ID_AGENDA")
    private Integer agendaId;

    @Column(name = "TITULO", length = 25, nullable = false)
    private String titulo;

    @Column(name = "DATA_HORA")
    private LocalDateTime dataHora;

    @Column(name = "ANOTACAO", length = 250)
    private String anotacao;

    @ManyToOne
    @JoinColumn(name = "ID_USUARIO")
    private Usuario usuario;

    //Construtor

    public Agenda() {
    }

    //GET E SET


    public Integer getAgendaId() {
        return agendaId;
    }

    public void setAgendaId(Integer agendaId) {
        this.agendaId = agendaId;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    public void setDataHora(LocalDateTime dataHora) {
        this.dataHora = dataHora;
    }

    public String getAnotacao() {
        return anotacao;
    }

    public void setAnotacao(String anotacao) {
        this.anotacao = anotacao;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }
}
