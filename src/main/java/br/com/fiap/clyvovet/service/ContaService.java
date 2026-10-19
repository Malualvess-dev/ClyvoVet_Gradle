package br.com.fiap.clyvovet.service;

import br.com.fiap.clyvovet.dto.request.CadastroContaRequest;
import br.com.fiap.clyvovet.exception.RecursoNaoEncontradoException;
import br.com.fiap.clyvovet.exception.RegraNegocioException;
import br.com.fiap.clyvovet.model.ProvedorLogin;
import br.com.fiap.clyvovet.model.Usuario;
import br.com.fiap.clyvovet.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDateTime;

@Service
@Transactional
public class ContaService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder codificadorDeSenha;
    private final Clock relogio;

    public ContaService(UsuarioRepository usuarioRepository, PasswordEncoder codificadorDeSenha, Clock relogio) {
        this.usuarioRepository = usuarioRepository;
        this.codificadorDeSenha = codificadorDeSenha;
        this.relogio = relogio;
    }

    public Usuario criarContaComSenha(CadastroContaRequest request) {
        String email = Usuario.normalizarEmail(request.getEmail());
        if (usuarioRepository.existsByEmail(email)) {
            throw new RegraNegocioException("Já existe uma conta cadastrada com este e-mail.");
        }
        String senhaCriptografada = codificadorDeSenha.encode(request.getSenha());
        return usuarioRepository.save(
                Usuario.comSenha(request.getNome(), email, senhaCriptografada, LocalDateTime.now(relogio)));
    }

    public Usuario obterOuCriarContaExterna(String nome, String email, ProvedorLogin provedor) {
        return usuarioRepository.findByEmail(Usuario.normalizarEmail(email))
                .orElseGet(() -> usuarioRepository.save(
                        Usuario.deProvedorExterno(nome, email, provedor, LocalDateTime.now(relogio))));
    }

    @Transactional(readOnly = true)
    public Usuario buscar(Long usuarioId) {
        return usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado."));
    }
}
