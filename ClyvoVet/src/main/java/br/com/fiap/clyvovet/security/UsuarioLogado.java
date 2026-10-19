package br.com.fiap.clyvovet.security;

import br.com.fiap.clyvovet.model.Perfil;
import br.com.fiap.clyvovet.model.Usuario;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public class UsuarioLogado implements UserDetails, OAuth2User {

    static final String PAPEL_PENDENTE = "PENDENTE";

    private final Long id;
    private final String nome;
    private final String email;
    private final String senha;
    private final Perfil perfil;
    private final Map<String, Object> atributos;

    private UsuarioLogado(Usuario usuario, Map<String, Object> atributos) {
        this.id = usuario.getId();
        this.nome = usuario.getNome();
        this.email = usuario.getEmail();
        this.senha = usuario.getSenha();
        this.perfil = usuario.getPerfil();
        this.atributos = Map.copyOf(atributos);
    }

    public static UsuarioLogado de(Usuario usuario) {
        return new UsuarioLogado(usuario, Map.of());
    }

    public static UsuarioLogado de(Usuario usuario, Map<String, Object> atributosDoProvedor) {
        return new UsuarioLogado(usuario, atributosDoProvedor);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        String papel = perfil == null ? PAPEL_PENDENTE : perfil.name();
        return List.of(new SimpleGrantedAuthority("ROLE_" + papel));
    }

    public boolean isTutor() {
        return perfil == Perfil.TUTOR;
    }

    public boolean isVeterinario() {
        return perfil == Perfil.VETERINARIO;
    }

    public boolean isCadastroPendente() {
        return perfil == null;
    }

    public String getPrimeiroNome() {
        return nome.split(" ")[0];
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
    public String getName() {
        return email;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return atributos;
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

    public Perfil getPerfil() {
        return perfil;
    }
}
