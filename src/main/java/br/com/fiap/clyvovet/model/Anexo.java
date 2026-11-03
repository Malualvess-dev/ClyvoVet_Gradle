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

/** Documento do usuário (exame, laudo, carteira de vacinação) guardado por link. */
@Entity
@Table(name = "TB_ANEXO")
public class Anexo {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_ANEXO")
    @SequenceGenerator(name = "SEQ_ANEXO", sequenceName = "SEQ_ANEXO", allocationSize = 1)
    @Column(name = "ID_ANEXO")
    private Long id;

    @Column(name = "NOME_ARQUIVO", nullable = false, length = 255)
    private String nomeArquivo;

    @Column(name = "TIPO_ARQUIVO", nullable = false, length = 50)
    private String tipoArquivo;

    @Column(name = "URL", nullable = false, length = 255)
    private String url;

    @Column(name = "TAMANHO_KB")
    private Integer tamanhoKb;

    @Column(name = "DATA_CRIACAO", nullable = false)
    private LocalDateTime dataCriacao;

    @ManyToOne(optional = false)
    @JoinColumn(name = "ID_USUARIO", nullable = false)
    private Usuario usuario;

    protected Anexo() {
    }

    public Anexo(Usuario usuario, String nomeArquivo, String tipoArquivo, String url, Integer tamanhoKb,
                 LocalDateTime dataCriacao) {
        this.usuario = usuario;
        this.nomeArquivo = nomeArquivo.strip();
        this.tipoArquivo = tipoArquivo.strip();
        this.url = url.strip();
        this.tamanhoKb = tamanhoKb;
        this.dataCriacao = dataCriacao;
    }

    public Long getId() {
        return id;
    }

    public String getNomeArquivo() {
        return nomeArquivo;
    }

    public String getTipoArquivo() {
        return tipoArquivo;
    }

    public String getUrl() {
        return url;
    }

    public Integer getTamanhoKb() {
        return tamanhoKb;
    }

    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }

    public Usuario getUsuario() {
        return usuario;
    }
}
