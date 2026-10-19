package br.com.fiap.clyvovet.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "TB_NOTIFICACAO")
public class Notificacao {
    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "notificacao_sequence"
    )
    @SequenceGenerator(
            name = "notificacao_sequence",
            sequenceName = "NOTIFICACAO_SEQUENCE", allocationSize = 1
    )

    @Column(name = "ID_NOTIFICACAO")
    private Integer notificacaoId;

    @Column(name = "MENSAGEM", length = 200, nullable = false)
    private String mensagem;

    @Column(name = "DATA_ENVIO")
    private LocalDateTime dataEnvio;

    @Column(name = "LIDA", length = 1)
    private String lida;

    @ManyToOne
    @JoinColumn(name = "ID_USUARIO")
    private Usuario usuario;

    //Construtor

    public Notificacao() {
    }

    //GET E SET

    public Integer getNotificacaoId() {
        return notificacaoId;
    }

    public void setNotificacaoId(Integer notificacaoId) {
        this.notificacaoId = notificacaoId;
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }

    public LocalDateTime getDataEnvio() {
        return dataEnvio;
    }

    public void setDataEnvio(LocalDateTime dataEnvio) {
        this.dataEnvio = dataEnvio;
    }

    public String getLida() {
        return lida;
    }

    public void setLida(String lida) {
        this.lida = lida;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }
}
