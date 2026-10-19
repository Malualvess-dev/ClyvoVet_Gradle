package br.com.fiap.clyvovet.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "TB_CV_NOTIFICACAO")
public class Notificacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_NOTIFICACAO")
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "ID_USUARIO", nullable = false)
    private Usuario usuario;

    @Column(name = "MENSAGEM", nullable = false, length = 250)
    private String mensagem;

    @Column(name = "DATA_ENVIO", nullable = false)
    private LocalDateTime dataEnvio;

    @Column(name = "DATA_LEITURA")
    private LocalDateTime dataLeitura;

    protected Notificacao() {
    }

    public Notificacao(Usuario usuario, String mensagem, LocalDateTime dataEnvio) {
        this.usuario = usuario;
        this.mensagem = mensagem;
        this.dataEnvio = dataEnvio;
    }

    public void marcarComoLida(LocalDateTime agora) {
        if (!isLida()) {
            dataLeitura = agora;
        }
    }

    public boolean isLida() {
        return dataLeitura != null;
    }

    public Long getId() {
        return id;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public String getMensagem() {
        return mensagem;
    }

    public LocalDateTime getDataEnvio() {
        return dataEnvio;
    }

    public LocalDateTime getDataLeitura() {
        return dataLeitura;
    }
}
