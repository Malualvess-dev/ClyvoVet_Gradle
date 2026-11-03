package br.com.fiap.clyvovet.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "TB_RACA")
public class Raca {

    @Id
    @Column(name = "ID_RACA")
    private Long id;

    @Column(name = "NOME", nullable = false, length = 100)
    private String nome;

    @Enumerated(EnumType.STRING)
    @Column(name = "PORTE", nullable = false, length = 10)
    private Porte porte;

    @ManyToOne(optional = false)
    @JoinColumn(name = "ID_ESPECIE", nullable = false)
    private Especie especie;

    protected Raca() {
    }

    public Raca(Long id, String nome, Porte porte, Especie especie) {
        this.id = id;
        this.nome = nome;
        this.porte = porte;
        this.especie = especie;
    }

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public Porte getPorte() {
        return porte;
    }

    public Especie getEspecie() {
        return especie;
    }
}
