package br.com.fiap.clyvovet.model;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "TB_TUTOR")
public class Tutor {
    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "tutor_sequence"
    )
    @SequenceGenerator(
            name = "tutor_sequence",
            sequenceName = "TUTOR_SEQUENCE", allocationSize = 1
    )

    @Column(name = "ID_TUTOR")
    private Integer tutorId;

    @Column(name = "NOME_TUTOR", length = 100, nullable = false)
    private String nomeTutor;

    @Column(name = "CPF", length = 14, nullable = false)
    private String cpf;

    @Column(name = "TELEFONE", length = 20)
    private String telefone;

    @Column(name = "DT_NASCIMENTO")
    private LocalDate dataNascimento;

    @ManyToOne
    @JoinColumn(name = "ID_USUARIO")
    private Usuario usuario;

    //Construtor

    public Tutor() {
    }

    //GET E SET


    public Integer getTutorId() {
        return tutorId;
    }

    public void setTutorId(Integer tutorId) {
        this.tutorId = tutorId;
    }

    public String getNomeTutor() {
        return nomeTutor;
    }

    public void setNomeTutor(String nomeTutor) {
        this.nomeTutor = nomeTutor;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
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
