package br.com.fiap.clyvovet.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "TB_ANEXO")
public class Anexo {
    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "anexo_sequence"
    )
    @SequenceGenerator(
            name = "anexo_sequence",
            sequenceName = "ANEXO_SEQUENCE", allocationSize = 1
    )

    @Column(name = "ID_ANEXO")
    private Integer anexoId;

    @Column(name = "NOME_ARQUIVO", length = 255, nullable = false)
    private String nomeArquivo;

    @Enumerated(EnumType.STRING)
    @Column(name = "TIPO_ARQUIVO", nullable = false)
    private TipoArquivo tipoArquivo;

    @Column(name = "URL", length = 255)
    private String url;

    @Column(name = "TAMANHO_KB")
    private Double tamanhoKb;

    @Column(name = "DATA_CRIACAO")
    private LocalDateTime dataCriacao;

    @ManyToOne
    @JoinColumn(name = "ID_USUARIO")
    private Usuario usuario;

    //Construtor Vazio

    public Anexo() {
    }

    //GET E SET


    public Integer getAnexoId() {
        return anexoId;
    }

    public void setAnexoId(Integer anexoId) {
        this.anexoId = anexoId;
    }

    public String getNomeArquivo() {
        return nomeArquivo;
    }

    public void setNomeArquivo(String nomeArquivo) {
        this.nomeArquivo = nomeArquivo;
    }

    public TipoArquivo getTipoArquivo() {
        return tipoArquivo;
    }

    public void setTipoArquivo(TipoArquivo tipoArquivo) {
        this.tipoArquivo = tipoArquivo;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Double getTamanhoKb() {
        return tamanhoKb;
    }

    public void setTamanhoKb(Double tamanhoKb) {
        this.tamanhoKb = tamanhoKb;
    }

    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(LocalDateTime dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }
}
