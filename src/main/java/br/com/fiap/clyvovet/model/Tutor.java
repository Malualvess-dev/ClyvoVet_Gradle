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
@Table(name = "TB_TUTOR")
public class Tutor {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_TUTOR")
    @SequenceGenerator(name = "SEQ_TUTOR", sequenceName = "SEQ_TUTOR", allocationSize = 1)
    @Column(name = "ID_TUTOR")
    private Long id;

    @OneToOne(optional = false)
    @JoinColumn(name = "ID_USUARIO", nullable = false, unique = true)
    private Usuario usuario;

    @Column(name = "NOME_TUTOR", nullable = false, length = 100)
    private String nome;

    @Column(name = "CPF", nullable = false, length = 14, unique = true)
    private String cpf;

    @Column(name = "TELEFONE", nullable = false, length = 15)
    private String telefone;

    @Column(name = "DT_NASCIMENTO", nullable = false)
    private LocalDate dataNascimento;

    protected Tutor() {
    }

    public Tutor(Usuario usuario, String cpf, String telefone, LocalDate dataNascimento) {
        this.usuario = usuario;
        this.nome = usuario.getNome();
        this.cpf = cpf;
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

    public String getCpf() {
        return cpf;
    }

    public String getTelefone() {
        return telefone;
    }

    public LocalDate getDataNascimento() {
        return dataNascimento;
    }
}
