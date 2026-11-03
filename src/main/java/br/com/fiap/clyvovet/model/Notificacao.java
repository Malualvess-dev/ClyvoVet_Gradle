package br.com.fiap.clyvovet.model;

import br.com.fiap.clyvovet.model.conversor.SimNaoConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "TB_NOTIFICACAO")
public class Notificacao {

    static final int TAMANHO_MAXIMO_MENSAGEM = 200;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_NOTIFICACAO")
    @SequenceGenerator(name = "SEQ_NOTIFICACAO", sequenceName = "SEQ_NOTIFICACAO", allocationSize = 1)
    @Column(name = "ID_NOTIFICACAO")
    private Long id;

    @Column(name = "MENSAGEM", nullable = false, length = TAMANHO_MAXIMO_MENSAGEM)
    private String mensagem;

    @Column(name = "DATA_ENVIO", nullable = false)
    private LocalDateTime dataEnvio;

    @Convert(converter = SimNaoConverter.class)
    @Column(name = "LIDA", nullable = false, length = 1)
    private boolean lida;

    @ManyToOne(optional = false)
    @JoinColumn(name = "ID_USUARIO", nullable = false)
    private Usuario usuario;

    protected Notificacao() {
    }

    public Notificacao(Usuario usuario, String mensagem, LocalDateTime dataEnvio) {
        this.usuario = usuario;
        this.mensagem = resumir(mensagem);
        this.dataEnvio = dataEnvio;
        this.lida = false;
    }

    private static String resumir(String mensagem) {
        return mensagem.length() <= TAMANHO_MAXIMO_MENSAGEM
                ? mensagem
                : mensagem.substring(0, TAMANHO_MAXIMO_MENSAGEM - 1) + "…";
    }

    public void marcarComoLida() {
        lida = true;
    }

    public boolean isLida() {
        return lida;
    }

    public Long getId() {
        return id;
    }

    public String getMensagem() {
        return mensagem;
    }

    public LocalDateTime getDataEnvio() {
        return dataEnvio;
    }

    public Usuario getUsuario() {
        return usuario;
    }
}
