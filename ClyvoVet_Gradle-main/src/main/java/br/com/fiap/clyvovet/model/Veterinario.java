package br.com.fiap.clyvovet.model;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "TB_VETERINARIO")
public class Veterinario {

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "veterinario_sequence"
    )
    @SequenceGenerator(
            name = "veterinario_sequence",
            sequenceName = "VETERINARIO_SEQUENCE",
            allocationSize = 1
    )
    @Column(name = "ID_VETERINARIO")
    private Integer veterinarioId;

    // Coluna NOME
    @Column(
            name = "NOME",
            length = 100,
            nullable = false
    )
    private String nomeVeterinario;

    // Coluna NOME_VETERINARIO
    @Column(
            name = "NOME_VETERINARIO",
            length = 100,
            nullable = false
    )
    private String nomeVeterinarioBanco;

    @Column(
            name = "CRMV",
            length = 20,
            nullable = false,
            unique = true
    )
    private String crmv;

    @Column(
            name = "TELEFONE",
            length = 20
    )
    private String telefone;

    @Column(
            name = "ESPECIALIDADE",
            length = 100
    )
    private String especialidade;

    @Column(
            name = "DT_NASCIMENTO",
            nullable = false
    )
    private LocalDate dataNascimento;

    @ManyToOne
    @JoinColumn(
            name = "ID_USUARIO",
            nullable = false
    )
    private Usuario usuario;

    // CONSTRUTOR

    public Veterinario() {
    }

    // GETTERS E SETTERS

    public Integer getVeterinarioId() {
        return veterinarioId;
    }

    public void setVeterinarioId(Integer veterinarioId) {
        this.veterinarioId = veterinarioId;
    }

    public String getNomeVeterinario() {
        return nomeVeterinario;
    }

    public void setNomeVeterinario(String nomeVeterinario) {
        this.nomeVeterinario = nomeVeterinario;
    }

    public String getNomeVeterinarioBanco() {
        return nomeVeterinarioBanco;
    }

    public void setNomeVeterinarioBanco(String nomeVeterinarioBanco) {
        this.nomeVeterinarioBanco = nomeVeterinarioBanco;
    }

    public String getCrmv() {
        return crmv;
    }

    public void setCrmv(String crmv) {
        this.crmv = crmv;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getEspecialidade() {
        return especialidade;
    }

    public void setEspecialidade(String especialidade) {
        this.especialidade = especialidade;
    }

    public LocalDate getDataNascimento() {
        return dataNascimento;
    }

    public void setDataNascimento(LocalDate dataNascimento) {
        this.dataNascimento = dataNascimento;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }
}