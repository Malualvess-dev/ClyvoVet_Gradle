package br.com.fiap.clyvovet.service;

import br.com.fiap.clyvovet.dto.request.TutorRequest;
import br.com.fiap.clyvovet.dto.response.TutorResponse;
import br.com.fiap.clyvovet.exception.ResourceNotFoundException;
import br.com.fiap.clyvovet.mapper.TutorMapper;
import br.com.fiap.clyvovet.model.Tutor;
import br.com.fiap.clyvovet.model.Usuario;
import br.com.fiap.clyvovet.repository.TutorRepository;
import br.com.fiap.clyvovet.repository.UsuarioRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class TutorService {

    private final TutorRepository tutorRepository;
    private final UsuarioRepository usuarioRepository;
    private final TutorMapper tutorMapper;

    public TutorService(
            TutorRepository tutorRepository,
            UsuarioRepository usuarioRepository,
            TutorMapper tutorMapper
    ) {
        this.tutorRepository = tutorRepository;
        this.usuarioRepository = usuarioRepository;
        this.tutorMapper = tutorMapper;
    }

    // CREATE
    public TutorResponse create(TutorRequest request) {

        Usuario usuario = usuarioRepository
                .findById(request.usuarioId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Usuário não encontrado"
                        )
                );

        Tutor tutor = new Tutor();

        tutor.setNomeTutor(request.nomeTutor());
        tutor.setCpf(request.cpf());
        tutor.setTelefone(request.telefone());
        tutor.setDataNascimento(request.dataNascimento());
        tutor.setUsuario(usuario);

        return tutorMapper.tutorToResponse(
                tutorRepository.save(tutor)
        );
    }

    // READ POR ID
    public TutorResponse readTutor(Integer id) {

        Tutor tutor = tutorRepository
                .findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Tutor não encontrado"
                        )
                );

        return tutorMapper.tutorToResponse(tutor);
    }

    // READ TODOS
    public Page<TutorResponse> read(Pageable pageable) {

        Pageable pageableSemOrdenacao =
                PageRequest.of(
                        pageable.getPageNumber(),
                        pageable.getPageSize()
                );

        Page<TutorResponse> tutores =
                tutorRepository
                        .findAll(pageableSemOrdenacao)
                        .map(tutorMapper::tutorToResponse);

        if (tutores.isEmpty()) {
            throw new ResourceNotFoundException(
                    "Não possui nenhum tutor cadastrado"
            );
        }

        return tutores;
    }

    // READ POR CPF
    public TutorResponse readByCpf(String cpf) {

        Tutor tutor = tutorRepository
                .findByCpf(cpf)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Tutor não encontrado pelo CPF"
                        )
                );

        return tutorMapper.tutorToResponse(tutor);
    }

    // UPDATE
    public TutorResponse update(
            Integer id,
            TutorRequest request
    ) {

        Tutor tutor = tutorRepository
                .findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Tutor não encontrado"
                        )
                );

        Usuario usuario = usuarioRepository
                .findById(request.usuarioId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Usuário não encontrado"
                        )
                );

        tutor.setNomeTutor(request.nomeTutor());
        tutor.setCpf(request.cpf());
        tutor.setTelefone(request.telefone());
        tutor.setDataNascimento(request.dataNascimento());
        tutor.setUsuario(usuario);

        return tutorMapper.tutorToResponse(
                tutorRepository.save(tutor)
        );
    }

    // DELETE
    public void delete(Integer id) {

        Tutor tutor = tutorRepository
                .findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Tutor não encontrado"
                        )
                );

        tutorRepository.delete(tutor);
    }
}