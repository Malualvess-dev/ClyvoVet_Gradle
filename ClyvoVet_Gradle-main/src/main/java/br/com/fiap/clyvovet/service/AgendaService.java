package br.com.fiap.clyvovet.service;

import br.com.fiap.clyvovet.dto.request.AgendaRequest;
import br.com.fiap.clyvovet.dto.response.AgendaResponse;
import br.com.fiap.clyvovet.exception.ResourceNotFoundException;
import br.com.fiap.clyvovet.mapper.AgendaMapper;
import br.com.fiap.clyvovet.model.Agenda;
import br.com.fiap.clyvovet.model.Usuario;
import br.com.fiap.clyvovet.repository.AgendaRepository;
import br.com.fiap.clyvovet.repository.UsuarioRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AgendaService {

    private final AgendaRepository agendaRepository;
    private final UsuarioRepository usuarioRepository;
    private final AgendaMapper agendaMapper;

    public AgendaService(
            AgendaRepository agendaRepository,
            UsuarioRepository usuarioRepository,
            AgendaMapper agendaMapper
    ) {
        this.agendaRepository = agendaRepository;
        this.usuarioRepository = usuarioRepository;
        this.agendaMapper = agendaMapper;
    }

    // CREATE
    public AgendaResponse create(AgendaRequest request) {

        Usuario usuario = usuarioRepository
                .findById(request.usuarioId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Usuário não encontrado"
                        )
                );

        Agenda agenda = new Agenda();

        agenda.setTitulo(request.titulo());
        agenda.setDataHora(request.dataHora());
        agenda.setAnotacao(request.anotacao());
        agenda.setUsuario(usuario);

        return agendaMapper.agendaToResponse(
                agendaRepository.save(agenda)
        );
    }

    // READ POR ID
    public AgendaResponse readAgenda(Integer id) {

        Agenda agenda = agendaRepository
                .findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Agenda não encontrada"
                        )
                );

        return agendaMapper.agendaToResponse(agenda);
    }

    // READ TODOS
    public Page<AgendaResponse> read(Pageable pageable) {

        Pageable pageableSemOrdenacao =
                PageRequest.of(
                        pageable.getPageNumber(),
                        pageable.getPageSize()
                );

        Page<AgendaResponse> agendas =
                agendaRepository
                        .findAll(pageableSemOrdenacao)
                        .map(agendaMapper::agendaToResponse);

        if (agendas.isEmpty()) {
            throw new ResourceNotFoundException(
                    "Não possui nenhuma agenda cadastrada"
            );
        }

        return agendas;
    }

    // READ POR PERÍODO
    public Page<AgendaResponse> readByPeriodo(
            LocalDateTime inicio,
            LocalDateTime fim,
            Pageable pageable
    ) {

        Pageable pageableSemOrdenacao =
                PageRequest.of(
                        pageable.getPageNumber(),
                        pageable.getPageSize()
                );

        Page<AgendaResponse> agendas =
                agendaRepository
                        .findByDataHoraBetween(
                                inicio,
                                fim,
                                pageableSemOrdenacao
                        )
                        .map(agendaMapper::agendaToResponse);

        if (agendas.isEmpty()) {
            throw new ResourceNotFoundException(
                    "Nenhuma agenda encontrada no período informado"
            );
        }

        return agendas;
    }

    // UPDATE
    public AgendaResponse update(
            Integer id,
            AgendaRequest request
    ) {

        Agenda agenda = agendaRepository
                .findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Agenda não encontrada"
                        )
                );

        Usuario usuario = usuarioRepository
                .findById(request.usuarioId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Usuário não encontrado"
                        )
                );

        agenda.setTitulo(request.titulo());
        agenda.setDataHora(request.dataHora());
        agenda.setAnotacao(request.anotacao());
        agenda.setUsuario(usuario);

        return agendaMapper.agendaToResponse(
                agendaRepository.save(agenda)
        );
    }

    // DELETE
    public void delete(Integer id) {

        Agenda agenda = agendaRepository
                .findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Agenda não encontrada"
                        )
                );

        agendaRepository.delete(agenda);
    }
}