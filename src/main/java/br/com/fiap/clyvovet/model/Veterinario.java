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
@Table(name = "TB_CV_VETERINARIO")
public class Veterinario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_VETERINARIO")
    private Long id;

    @OneToOne(optional = false)
    @JoinColumn(name = "ID_USUARIO", nullable = false, unique = true)
    private Usuario usuario;

    @Column(name = "CRMV", nullable = false, length = 20, unique = true)
    private String crmv;

    @Column(name = "ESPECIALIDADE", nullable = false, length = 100)
    private String especialidade;

    @Column(name = "TELEFONE", nullable = false, length = 20)
    private String telefone;

    protected Veterinario() {
    }

    public Veterinario(Usuario usuario, String crmv, String especialidade, String telefone) {
        this.usuario = usuario;
        this.crmv = crmv;
        this.especialidade = especialidade;
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

    public String getCrmv() {
        return crmv;
    }

    public String getEspecialidade() {
        return especialidade;
    }

    public String getTelefone() {
        return telefone;
    }
}
