package br.com.fiap.clyvovet.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "TB_BAIRRO")
public class Bairro {

    @Id
    @Column(name = "ID_BAIRRO")
    private Long id;

    @Column(name = "NOME", nullable = false, length = 100)
    private String nome;

    @ManyToOne(optional = false)
    @JoinColumn(name = "ID_CIDADE", nullable = false)
    private Cidade cidade;

    protected Bairro() {
    }

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public Cidade getCidade() {
        return cidade;
    }

    /** Ex.: "Pinheiros · São Paulo/SP". */
    public String getDescricaoCompleta() {
        return "%s · %s/%s".formatted(nome, cidade.getNome(), cidade.getEstado().getUf());
    }
}
