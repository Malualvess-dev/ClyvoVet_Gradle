package br.com.fiap.clyvovet.model;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "TB_ESTADO")
public class Estado {
    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "estado_sequence"
    )
    @SequenceGenerator(
            name = "estado_sequence",
            sequenceName = "ESTADO_SEQUENCE",
            allocationSize = 1
    )
    @Column(name = "ID_ESTADO")
    private Integer estadoId;

    @Column(name = "NOME", length = 50, nullable = false)
    private String nome;

    @Column(name = "UF", length = 2, nullable = false)
    private String uf;

    @OneToMany(
            mappedBy = "estado",
            cascade = {CascadeType.PERSIST, CascadeType.MERGE}
    )
    private List<Cidade> cidades = new ArrayList<>();


    //Construtor Vazio

    public Estado() {
    }


    //GET E SET


    public Integer getEstadoId() {
        return estadoId;
    }

    public void setEstadoId(Integer estadoId) {
        this.estadoId = estadoId;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getUf() {
        return uf;
    }

    public void setUf(String uf) {
        this.uf = uf;
    }

    public List<Cidade> getCidades() {
        return cidades;
    }

    public void setCidades(List<Cidade> cidades) {
        this.cidades = cidades;
    }
}
