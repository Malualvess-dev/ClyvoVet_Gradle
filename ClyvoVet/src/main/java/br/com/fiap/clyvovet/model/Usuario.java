package br.com.fiap.clyvovet.model;

import br.com.fiap.clyvovet.exception.RegraNegocioException;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.Locale;

@Entity
@Table(name = "TB_CV_USUARIO")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_USUARIO")
    private Long id;

    @Column(name = "NOME", nullable = false, length = 100)
    private String nome;

    @Column(name = "EMAIL", nullable = false, length = 120, unique = true)
    private String email;

    @Column(name = "SENHA", length = 100)
    private String senha;

    @Enumerated(EnumType.STRING)
    @Column(name = "PERFIL", length = 20)
    private Perfil perfil;

    @Enumerated(EnumType.STRING)
    @Column(name = "PROVEDOR", nullable = false, length = 20)
    private ProvedorLogin provedor;

    @Column(name = "DATA_CRIACAO", nullable = false)
    private LocalDateTime dataCriacao;

    protected Usuario() {
    }

    private Usuario(String nome, String email, String senha, ProvedorLogin provedor, LocalDateTime dataCriacao) {
        this.nome = nome.strip();
        this.email = normalizarEmail(email);
        this.senha = senha;
        this.provedor = provedor;
        this.dataCriacao = dataCriacao;
    }

    public static Usuario comSenha(String nome, String email, String senhaCriptografada, LocalDateTime agora) {
        return new Usuario(nome, email, senhaCriptografada, ProvedorLogin.LOCAL, agora);
    }

    public static Usuario deProvedorExterno(String nome, String email, ProvedorLogin provedor, LocalDateTime agora) {
        return new Usuario(nome, email, null, provedor, agora);
    }

    public static String normalizarEmail(String email) {
        return email.strip().toLowerCase(Locale.ROOT);
    }

    public void definirPerfil(Perfil novoPerfil) {
        if (possuiPerfil()) {
            throw new RegraNegocioException("O perfil desta conta já foi definido.");
        }
        this.perfil = novoPerfil;
    }

    public boolean possuiPerfil() {
        return perfil != null;
    }

    public boolean possuiSenha() {
        return senha != null;
    }

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getEmail() {
        return email;
    }

    public String getSenha() {
        return senha;
    }

    public Perfil getPerfil() {
        return perfil;
    }

    public ProvedorLogin getProvedor() {
        return provedor;
    }

    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }
}
