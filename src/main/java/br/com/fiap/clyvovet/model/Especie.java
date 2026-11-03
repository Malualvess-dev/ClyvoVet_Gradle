package br.com.fiap.clyvovet.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "TB_ESPECIE")
public class Especie {

    @Id
    @Column(name = "ID_ESPECIE")
    private Long id;

    @Column(name = "NOME", nullable = false, length = 50)
    private String nome;

    protected Especie() {
    }

    public Especie(Long id, String nome) {
        this.id = id;
        this.nome = nome;
    }

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }
}
