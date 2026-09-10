package br.com.fiap.clyvovet.service;

import br.com.fiap.clyvovet.dto.request.EstadoRequest;
import br.com.fiap.clyvovet.dto.response.EstadoResponse;
import br.com.fiap.clyvovet.exception.ResourceNotFoundException;
import br.com.fiap.clyvovet.mapper.EstadoMapper;
import br.com.fiap.clyvovet.model.Estado;
import br.com.fiap.clyvovet.repository.EstadoRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class EstadoService {

    private final EstadoRepository estadoRepository;
    private final EstadoMapper estadoMapper;

    public EstadoService(
            EstadoRepository estadoRepository,
            EstadoMapper estadoMapper
    ) {
        this.estadoRepository = estadoRepository;
        this.estadoMapper = estadoMapper;
    }

    // CREATE
    public EstadoResponse create(EstadoRequest request) {

        Estado estado = new Estado();

        BeanUtils.copyProperties(
                request,
                estado
        );

        return estadoMapper.estadoToResponse(
                estadoRepository.save(estado)
        );
    }

    // READ POR ID
    public EstadoResponse readEstado(Integer id) {

        Estado estado = estadoRepository
                .findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Estado não encontrado"
                        )
                );

        return estadoMapper.estadoToResponse(estado);
    }

    // READ TODOS
    public Page<EstadoResponse> read(Pageable pageable) {

        Pageable pageableSemOrdenacao =
                PageRequest.of(
                        pageable.getPageNumber(),
                        pageable.getPageSize()
                );

        Page<EstadoResponse> estados =
                estadoRepository
                        .findAll(pageableSemOrdenacao)
                        .map(estadoMapper::estadoToResponse);

        if (estados.isEmpty()) {
            throw new ResourceNotFoundException(
                    "Não possui nenhum estado cadastrado"
            );
        }

        return estados;
    }

    // READ POR UF
    public EstadoResponse readByUf(String uf) {

        Estado estado = estadoRepository
                .findByUf(uf)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Estado não encontrado pela UF"
                        )
                );

        return estadoMapper.estadoToResponse(estado);
    }

    // UPDATE
    public EstadoResponse update(
            Integer id,
            EstadoRequest request
    ) {

        Estado estado = estadoRepository
                .findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Estado não encontrado"
                        )
                );

        BeanUtils.copyProperties(
                request,
                estado
        );

        return estadoMapper.estadoToResponse(
                estadoRepository.save(estado)
        );
    }

    // DELETE
    public void delete(Integer id) {

        Estado estado = estadoRepository
                .findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Estado não encontrado"
                        )
                );

        estadoRepository.delete(estado);
    }
}