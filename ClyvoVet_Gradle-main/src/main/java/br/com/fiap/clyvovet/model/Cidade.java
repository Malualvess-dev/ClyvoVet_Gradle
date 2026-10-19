package br.com.fiap.clyvovet.model;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "TB_CIDADE")
public class Cidade {
    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "cidade_sequence"
    )
    @SequenceGenerator(
            name = "cidade_sequence",
            sequenceName = "CIDADE_SEQUENCE",
            allocationSize = 1
    )
    @Column(name = "ID_CIDADE")
    private Integer cidadeId;

    @Column(name = "NOME", length = 100, nullable = false)
    private String nome;

    @ManyToOne
    @JoinColumn(name = "ID_ESTADO")
    private Estado estado;

    @OneToMany(
            mappedBy = "cidade",
            cascade = {CascadeType.PERSIST, CascadeType.MERGE}
    )
    private List<Bairro> bairros = new ArrayList<>();



    //Construtor Vazio

    public Cidade() {
    }

    //GET E SET


    public Integer getCidadeId() {
        return cidadeId;
    }

    public void setCidadeId(Integer cidadeId) {
        this.cidadeId = cidadeId;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Estado getEstado() {
        return estado;
    }

    public void setEstado(Estado estado) {
        this.estado = estado;
    }

    public List<Bairro> getBairros() {
        return bairros;
    }

    public void setBairros(List<Bairro> bairros) {
        this.bairros = bairros;
    }
}
