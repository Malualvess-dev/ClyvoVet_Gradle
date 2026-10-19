package br.com.fiap.clyvovet.model;

import jakarta.persistence.*;

@Entity
@Table(name = "TB_BAIRRO")
public class Bairro {
    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "bairro_sequence"
    )
    @SequenceGenerator(
            name = "bairro_sequence",
            sequenceName = "BAIRRO_SEQUENCE",
            allocationSize = 1
    )
    @Column(name = "ID_BAIRRO")
    private Integer bairroId;

    @Column(name = "NOME", length = 100, nullable = false)
    private String nome;

    @ManyToOne
    @JoinColumn(name = "ID_CIDADE")
    private Cidade cidade;

    //Construtor Vazio


    public Bairro() {
    }

    //GET E SET

    public Integer getBairroId() {
        return bairroId;
    }

    public void setBairroId(Integer bairroId) {
        this.bairroId = bairroId;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Cidade getCidade() {
        return cidade;
    }

    public void setCidade(Cidade cidade) {
        this.cidade = cidade;
    }
}
