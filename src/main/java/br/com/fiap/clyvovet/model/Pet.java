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
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "TB_PET")
public class Pet {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_PET")
    @SequenceGenerator(name = "SEQ_PET", sequenceName = "SEQ_PET", allocationSize = 1)
    @Column(name = "ID_PET")
    private Long id;

    @Column(name = "NOME", nullable = false, length = 50)
    private String nome;

    @Column(name = "DT_NASCIMENTO", nullable = false)
    private LocalDate dataNascimento;

    @Enumerated(EnumType.STRING)
    @Column(name = "SEXO", nullable = false, length = 1)
    private Sexo sexo;

    @Column(name = "FOTO_URL", length = 255)
    private String fotoUrl;

    @ManyToOne(optional = false)
    @JoinColumn(name = "ID_TUTOR", nullable = false)
    private Tutor tutor;

    @ManyToOne(optional = false)
    @JoinColumn(name = "ID_RACA", nullable = false)
    private Raca raca;

    @Column(name = "PESO_KG", precision = 5, scale = 2)
    private BigDecimal pesoKg;

    @Column(name = "OBSERVACOES", length = 500)
    private String observacoes;

    protected Pet() {
    }

    public Pet(Tutor tutor, String nome, Raca raca, Sexo sexo, LocalDate dataNascimento, BigDecimal pesoKg,
               String fotoUrl) {
        this.tutor = tutor;
        this.nome = nome.strip();
        this.raca = raca;
        this.sexo = sexo;
        this.dataNascimento = dataNascimento;
        this.pesoKg = pesoKg;
        this.fotoUrl = Texto.opcional(fotoUrl);
    }

    /** Peso aferido no atendimento. Se o veterinário não informar, mantém o peso atual. */
    public void atualizarPeso(BigDecimal pesoAferidoKg) {
        if (pesoAferidoKg != null) {
            this.pesoKg = pesoAferidoKg;
        }
    }

    public void atualizarDados(String novoNome, Raca novaRaca, Sexo novoSexo, LocalDate novaDataNascimento,
                               BigDecimal novoPesoKg) {
        this.nome = novoNome.strip();
        this.raca = novaRaca;
        this.sexo = novoSexo;
        this.dataNascimento = novaDataNascimento;
        this.pesoKg = novoPesoKg;
    }

    public void registrarObservacoes(String texto) {
        this.observacoes = Texto.opcional(texto);
    }

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public LocalDate getDataNascimento() {
        return dataNascimento;
    }

    public Sexo getSexo() {
        return sexo;
    }

    public String getFotoUrl() {
        return fotoUrl;
    }

    public Tutor getTutor() {
        return tutor;
    }

    public Raca getRaca() {
        return raca;
    }

    public Especie getEspecie() {
        return raca.getEspecie();
    }

    public BigDecimal getPesoKg() {
        return pesoKg;
    }

    public String getObservacoes() {
        return observacoes;
    }
}
