package br.com.fiap.clyvovet.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

import java.time.LocalDate;

@Entity
@Table(name = "TB_VETERINARIO")
public class Veterinario {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_VETERINARIO")
    @SequenceGenerator(name = "SEQ_VETERINARIO", sequenceName = "SEQ_VETERINARIO", allocationSize = 1)
    @Column(name = "ID_VETERINARIO")
    private Long id;

    @OneToOne(optional = false)
    @JoinColumn(name = "ID_USUARIO", nullable = false, unique = true)
    private Usuario usuario;

    @Column(name = "NOME", nullable = false, length = 100)
    private String nome;

    @Column(name = "CRMV", nullable = false, length = 14, unique = true)
    private String crmv;

    @Column(name = "TELEFONE", nullable = false, length = 15)
    private String telefone;

    @Column(name = "ESPECIALIDADE", nullable = false, length = 100)
    private String especialidade;

    @Column(name = "DT_NASCIMENTO", nullable = false)
    private LocalDate dataNascimento;

    protected Veterinario() {
    }

    public Veterinario(Usuario usuario, String crmv, String especialidade, String telefone, LocalDate dataNascimento) {
        this.usuario = usuario;
        this.nome = usuario.getNome();
        this.crmv = crmv;
        this.especialidade = especialidade;
        this.telefone = telefone;
        this.dataNascimento = dataNascimento;
    }

    public Long getId() {
        return id;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public String getNome() {
        return nome;
    }

    public String getCrmv() {
        return crmv;
    }

    public String getTelefone() {
        return telefone;
    }

    public String getEspecialidade() {
        return especialidade;
    }

    public LocalDate getDataNascimento() {
        return dataNascimento;
    }
}
