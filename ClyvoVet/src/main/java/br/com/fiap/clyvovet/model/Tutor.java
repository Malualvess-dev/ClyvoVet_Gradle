package br.com.fiap.clyvovet.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "TB_CV_TUTOR")
public class Tutor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_TUTOR")
    private Long id;

    @OneToOne(optional = false)
    @JoinColumn(name = "ID_USUARIO", nullable = false, unique = true)
    private Usuario usuario;

    @Column(name = "CPF", nullable = false, length = 14, unique = true)
    private String cpf;

    @Column(name = "TELEFONE", nullable = false, length = 20)
    private String telefone;

    protected Tutor() {
    }

    public Tutor(Usuario usuario, String cpf, String telefone) {
        this.usuario = usuario;
        this.cpf = cpf;
        this.telefone = telefone;
    }

    public String getNome() {
        return usuario.getNome();
    }

    public Long getId() {
        return id;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public String getCpf() {
        return cpf;
    }

    public String getTelefone() {
        return telefone;
    }
}
