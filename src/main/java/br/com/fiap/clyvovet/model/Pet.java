package br.com.fiap.clyvovet.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "TB_CV_PET")
public class Pet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_PET")
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "ID_TUTOR", nullable = false)
    private Tutor tutor;

    @Column(name = "NOME", nullable = false, length = 60)
    private String nome;

    @Enumerated(EnumType.STRING)
    @Column(name = "ESPECIE", nullable = false, length = 20)
    private Especie especie;

    @Column(name = "RACA", length = 60)
    private String raca;

    @Column(name = "DT_NASCIMENTO", nullable = false)
    private LocalDate dataNascimento;

    @Column(name = "PESO_KG", nullable = false, precision = 5, scale = 2)
    private BigDecimal pesoKg;

    protected Pet() {
    }

    public Pet(Tutor tutor, String nome, Especie especie, String raca, LocalDate dataNascimento, BigDecimal pesoKg) {
        this.tutor = tutor;
        this.nome = nome.strip();
        this.especie = especie;
        this.raca = raca;
        this.dataNascimento = dataNascimento;
        this.pesoKg = pesoKg;
    }

    public void atualizarPeso(BigDecimal pesoAferidoKg) {
        this.pesoKg = pesoAferidoKg;
    }

    public Long getId() {
        return id;
    }

    public Tutor getTutor() {
        return tutor;
    }

    public String getNome() {
        return nome;
    }

    public Especie getEspecie() {
        return especie;
    }

    public String getRaca() {
        return raca;
    }

    public LocalDate getDataNascimento() {
        return dataNascimento;
    }

    public BigDecimal getPesoKg() {
        return pesoKg;
    }
}
