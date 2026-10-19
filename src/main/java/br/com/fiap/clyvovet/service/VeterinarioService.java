package br.com.fiap.clyvovet.service;

import br.com.fiap.clyvovet.dto.request.PerfilVeterinarioRequest;
import br.com.fiap.clyvovet.dto.response.VeterinarioResponse;
import br.com.fiap.clyvovet.exception.RecursoNaoEncontradoException;
import br.com.fiap.clyvovet.exception.RegraNegocioException;
import br.com.fiap.clyvovet.mapper.VeterinarioMapper;
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
    private final VeterinarioMapper veterinarioMapper;

    public VeterinarioService(VeterinarioRepository veterinarioRepository, ContaService contaService,
                              VeterinarioMapper veterinarioMapper) {
        this.veterinarioRepository = veterinarioRepository;
        this.contaService = contaService;
        this.veterinarioMapper = veterinarioMapper;
    }

    public Usuario tornarVeterinario(Long usuarioId, PerfilVeterinarioRequest request) {
        if (veterinarioRepository.existsByCrmv(request.getCrmv())) {
            throw new RegraNegocioException("Este CRMV já está cadastrado.");
        }
        Usuario usuario = contaService.buscar(usuarioId);
        usuario.definirPerfil(Perfil.VETERINARIO);
        veterinarioRepository.save(new Veterinario(usuario, request.getCrmv(),
                request.getEspecialidade().strip(), request.getTelefone()));
        return usuario;
    }

    @Transactional(readOnly = true)
    public List<VeterinarioResponse> listar() {
        return veterinarioRepository.findAllByOrderByUsuario_NomeAsc().stream()
                .map(veterinarioMapper::paraResponse)
                .toList();
    }

    Veterinario buscar(Long veterinarioId) {
        return veterinarioRepository.findById(veterinarioId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Veterinário não encontrado."));
    }
}
