package br.com.fiap.clyvovet.model;

import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "TB_USUARIO")
public class Usuario implements UserDetails {

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "usuario_sequence"
    )
    @SequenceGenerator(
            name = "usuario_sequence",
            sequenceName = "USUARIO_SEQUENCE",
            allocationSize = 1
    )
    @Column(name = "ID_USUARIO")
    private Integer usuarioId;

    @Column(name = "EMAIL", length = 80, nullable = false)
    private String email;

    @Column(name = "SENHA", length = 100, nullable = false)
    private String senha;

    @Enumerated(EnumType.STRING)
    @Column(name = "TIPO_USER", nullable = false)
    private TipoUsuario tipoUser;

    @Column(name = "DATA_CRIACAO")
    private LocalDateTime dataCriacao;

    public Usuario() {
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        return List.of(
                new SimpleGrantedAuthority("ROLE_" + tipoUser.name())
        );

    }

    @Override
    public String getPassword() {
        return senha;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public Integer getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Integer usuarioId) {
        this.usuarioId = usuarioId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public TipoUsuario getTipoUser() {
        return tipoUser;
    }

    public void setTipoUser(TipoUsuario tipoUser) {
        this.tipoUser = tipoUser;
    }

    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(LocalDateTime dataCriacao) {
        this.dataCriacao = dataCriacao;
    }
}