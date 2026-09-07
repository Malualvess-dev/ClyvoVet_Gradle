package br.com.fiap.clyvovet.service;

import br.com.fiap.clyvovet.dto.request.VeterinarioRequest;
import br.com.fiap.clyvovet.dto.response.VeterinarioResponse;
import br.com.fiap.clyvovet.exception.ResourceNotFoundException;
import br.com.fiap.clyvovet.mapper.VeterinarioMapper;
import br.com.fiap.clyvovet.model.Usuario;
import br.com.fiap.clyvovet.model.Veterinario;
import br.com.fiap.clyvovet.repository.UsuarioRepository;
import br.com.fiap.clyvovet.repository.VeterinarioRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class VeterinarioService {

    private final VeterinarioRepository veterinarioRepository;
    private final UsuarioRepository usuarioRepository;
    private final VeterinarioMapper veterinarioMapper;

    public VeterinarioService(
            VeterinarioRepository veterinarioRepository,
            UsuarioRepository usuarioRepository,
            VeterinarioMapper veterinarioMapper
    ) {
        this.veterinarioRepository = veterinarioRepository;
        this.usuarioRepository = usuarioRepository;
        this.veterinarioMapper = veterinarioMapper;
    }

    // CREATE
    public VeterinarioResponse create(VeterinarioRequest request) {

        Usuario usuario = usuarioRepository
                .findById(request.usuarioId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Usuário não encontrado"
                        )
                );

        Veterinario veterinario = new Veterinario();

        // Preenche NOME
        veterinario.setNomeVeterinario(
                request.nomeVeterinario()
        );

        // Preenche NOME_VETERINARIO
        veterinario.setNomeVeterinarioBanco(
                request.nomeVeterinario()
        );

        veterinario.setCrmv(
                request.crmv()
        );

        veterinario.setTelefone(
                request.telefone()
        );

        veterinario.setEspecialidade(
                request.especialidade()
        );

        veterinario.setDataNascimento(
                request.dataNascimento()
        );

        veterinario.setUsuario(
                usuario
        );

        return veterinarioMapper.veterinarioToResponse(
                veterinarioRepository.save(veterinario)
        );
    }

    // READ POR ID
    public VeterinarioResponse readVeterinario(Integer id) {

        Veterinario veterinario =
                veterinarioRepository
                        .findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Veterinário não encontrado"
                                )
                        );

        return veterinarioMapper.veterinarioToResponse(
                veterinario
        );
    }

    // READ TODOS
    public Page<VeterinarioResponse> read(Pageable pageable) {

        Pageable pageableSemOrdenacao =
                PageRequest.of(
                        pageable.getPageNumber(),
                        pageable.getPageSize()
                );

        Page<VeterinarioResponse> veterinarios =
                veterinarioRepository
                        .findAll(pageableSemOrdenacao)
                        .map(veterinarioMapper::veterinarioToResponse);

        if (veterinarios.isEmpty()) {
            throw new ResourceNotFoundException(
                    "Não possui nenhum veterinário cadastrado"
            );
        }

        return veterinarios;
    }

    // READ POR CRMV
    public VeterinarioResponse readByCrmv(String crmv) {

        Veterinario veterinario =
                veterinarioRepository
                        .findByCrmv(crmv)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Veterinário não encontrado pelo CRMV"
                                )
                        );

        return veterinarioMapper.veterinarioToResponse(
                veterinario
        );
    }

    // UPDATE
    public VeterinarioResponse update(
            Integer id,
            VeterinarioRequest request
    ) {

        Veterinario veterinario =
                veterinarioRepository
                        .findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Veterinário não encontrado"
                                )
                        );

        Usuario usuario =
                usuarioRepository
                        .findById(request.usuarioId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Usuário não encontrado"
                                )
                        );

        // Atualiza NOME
        veterinario.setNomeVeterinario(
                request.nomeVeterinario()
        );

        // Atualiza NOME_VETERINARIO
        veterinario.setNomeVeterinarioBanco(
                request.nomeVeterinario()
        );

        veterinario.setCrmv(
                request.crmv()
        );

        veterinario.setTelefone(
                request.telefone()
        );

        veterinario.setEspecialidade(
                request.especialidade()
        );

        veterinario.setDataNascimento(
                request.dataNascimento()
        );

        veterinario.setUsuario(
                usuario
        );

        return veterinarioMapper.veterinarioToResponse(
                veterinarioRepository.save(veterinario)
        );
    }

    // DELETE
    public void delete(Integer id) {

        Veterinario veterinario =
                veterinarioRepository
                        .findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Veterinário não encontrado"
                                )
                        );

        veterinarioRepository.delete(
                veterinario
        );
    }
}