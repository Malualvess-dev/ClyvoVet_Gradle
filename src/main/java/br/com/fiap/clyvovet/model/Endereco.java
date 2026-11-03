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

import java.math.BigDecimal;

@Entity
@Table(name = "TB_ENDERECO")
public class Endereco {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_ENDERECO")
    @SequenceGenerator(name = "SEQ_ENDERECO", sequenceName = "SEQ_ENDERECO", allocationSize = 1)
    @Column(name = "ID_ENDERECO")
    private Long id;

    @Column(name = "LOGRADOURO", nullable = false, length = 120)
    private String logradouro;

    @Column(name = "NUMERO", nullable = false, length = 10)
    private String numero;

    @Column(name = "COMPLEMENTO", length = 50)
    private String complemento;

    @Column(name = "CEP", nullable = false, length = 9)
    private String cep;

    @Column(name = "LATITUDE", precision = 10, scale = 8)
    private BigDecimal latitude;

    @Column(name = "LONGITUDE", precision = 11, scale = 8)
    private BigDecimal longitude;

    @ManyToOne(optional = false)
    @JoinColumn(name = "ID_USUARIO", nullable = false)
    private Usuario usuario;

    @ManyToOne(optional = false)
    @JoinColumn(name = "ID_BAIRRO", nullable = false)
    private Bairro bairro;

    protected Endereco() {
    }

    public Endereco(Usuario usuario, Bairro bairro, String logradouro, String numero, String complemento,
                    String cep) {
        this.usuario = usuario;
        atualizar(bairro, logradouro, numero, complemento, cep);
    }

    public final void atualizar(Bairro novoBairro, String novoLogradouro, String novoNumero, String novoComplemento,
                                String novoCep) {
        this.bairro = novoBairro;
        this.logradouro = novoLogradouro.strip();
        this.numero = novoNumero.strip();
        this.complemento = Texto.opcional(novoComplemento);
        this.cep = formatarCep(novoCep);
        // As coordenadas pertenciam ao endereço anterior; a aplicação não faz geolocalização.
        this.latitude = null;
        this.longitude = null;
    }

    private static String formatarCep(String cep) {
        String digitos = cep.replaceAll("\\D", "");
        return digitos.substring(0, 5) + "-" + digitos.substring(5);
    }

    public Long getId() {
        return id;
    }

    public String getLogradouro() {
        return logradouro;
    }

    public String getNumero() {
        return numero;
    }

    public String getComplemento() {
        return complemento;
    }

    public String getCep() {
        return cep;
    }

    public BigDecimal getLatitude() {
        return latitude;
    }

    public BigDecimal getLongitude() {
        return longitude;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public Bairro getBairro() {
        return bairro;
    }
}
