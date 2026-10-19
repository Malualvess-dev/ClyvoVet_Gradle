package br.com.fiap.clyvovet.service;

import br.com.fiap.clyvovet.dto.PerfilVeterinarioForm;
import br.com.fiap.clyvovet.exception.RecursoNaoEncontradoException;
import br.com.fiap.clyvovet.exception.RegraNegocioException;
import br.com.fiap.clyvovet.model.Perfil;
import br.com.fiap.clyvovet.model.Usuario;
import br.com.fiap.clyvovet.model.Veterinario;
import br.com.fiap.clyvovet.repository.VeterinarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class VeterinarioService {

    private final VeterinarioRepository veterinarioRepository;
    private final ContaService contaService;

    public VeterinarioService(VeterinarioRepository veterinarioRepository, ContaService contaService) {
        this.veterinarioRepository = veterinarioRepository;
        this.contaService = contaService;
    }

    public Usuario tornarVeterinario(Long usuarioId, PerfilVeterinarioForm form) {
        if (veterinarioRepository.existsByCrmv(form.getCrmv())) {
            throw new RegraNegocioException("Este CRMV já está cadastrado.");
        }
        Usuario usuario = contaService.buscar(usuarioId);
        usuario.definirPerfil(Perfil.VETERINARIO);
        veterinarioRepository.save(
                new Veterinario(usuario, form.getCrmv(), form.getEspecialidade().strip(), form.getTelefone()));
        return usuario;
    }

    @Transactional(readOnly = true)
    public List<Veterinario> listar() {
        return veterinarioRepository.findAllByOrderByUsuario_NomeAsc();
    }

    @Transactional(readOnly = true)
    public Veterinario buscar(Long veterinarioId) {
        return veterinarioRepository.findById(veterinarioId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Veterinário não encontrado."));
    }
}
