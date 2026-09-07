package br.com.fiap.clyvovet.service;

import br.com.fiap.clyvovet.dto.request.LoginRequest;
import br.com.fiap.clyvovet.dto.response.LoginResponse;
import br.com.fiap.clyvovet.model.Usuario;
import br.com.fiap.clyvovet.repository.UsuarioRepository;
import br.com.fiap.clyvovet.security.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService
    ) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public LoginResponse login(LoginRequest request) {

        Usuario usuario = usuarioRepository
                .findByEmail(request.email())
                .orElseThrow(() ->
                        new RuntimeException("Usuário não encontrado")
                );

        if (!passwordEncoder.matches(
                request.senha(),
                usuario.getSenha()
        )) {
            throw new RuntimeException("Senha inválida");
        }

        String token = jwtService.generateToken(usuario);

        return new LoginResponse(
                token,
                usuario.getTipoUser().name()
        );
    }
}