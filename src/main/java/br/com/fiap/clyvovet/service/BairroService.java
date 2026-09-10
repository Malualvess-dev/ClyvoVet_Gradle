package br.com.fiap.clyvovet.service;

import br.com.fiap.clyvovet.dto.request.BairroRequest;
import br.com.fiap.clyvovet.dto.response.BairroResponse;
import br.com.fiap.clyvovet.exception.ResourceNotFoundException;
import br.com.fiap.clyvovet.mapper.BairroMapper;
import br.com.fiap.clyvovet.model.Bairro;
import br.com.fiap.clyvovet.model.Cidade;
import br.com.fiap.clyvovet.repository.BairroRepository;
import br.com.fiap.clyvovet.repository.CidadeRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class BairroService {

    private final BairroRepository bairroRepository;
    private final CidadeRepository cidadeRepository;
    private final BairroMapper bairroMapper;

    public BairroService(
            BairroRepository bairroRepository,
            CidadeRepository cidadeRepository,
            BairroMapper bairroMapper
    ) {
        this.bairroRepository = bairroRepository;
        this.cidadeRepository = cidadeRepository;
        this.bairroMapper = bairroMapper;
    }

    // CREATE
    public BairroResponse create(BairroRequest request) {

        Cidade cidade = cidadeRepository
                .findById(request.cidadeId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Cidade não encontrada"
                        )
                );

        Bairro bairro = new Bairro();
        bairro.setNome(request.nome());
        bairro.setCidade(cidade);

        return bairroMapper.bairroToResponse(
                bairroRepository.save(bairro)
        );
    }

    // READ POR ID
    public BairroResponse readBairro(Integer id) {

        Bairro bairro = bairroRepository
                .findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Bairro não encontrado"
                        )
                );

        return bairroMapper.bairroToResponse(bairro);
    }

    // READ TODOS
    public Page<BairroResponse> read(Pageable pageable) {

        Pageable pageableSemOrdenacao =
                PageRequest.of(
                        pageable.getPageNumber(),
                        pageable.getPageSize()
                );

        Page<BairroResponse> bairros =
                bairroRepository
                        .findAll(pageableSemOrdenacao)
                        .map(bairroMapper::bairroToResponse);

        if (bairros.isEmpty()) {
            throw new ResourceNotFoundException(
                    "Não possui nenhum bairro cadastrado"
            );
        }

        return bairros;
    }

    // READ POR NOME
    public Page<BairroResponse> readByNome(
            String nome,
            Pageable pageable
    ) {

        Pageable pageableSemOrdenacao =
                PageRequest.of(
                        pageable.getPageNumber(),
                        pageable.getPageSize()
                );

        Page<BairroResponse> bairros =
                bairroRepository
                        .findByNomeContainingIgnoreCase(
                                nome,
                                pageableSemOrdenacao
                        )
                        .map(bairroMapper::bairroToResponse);

        if (bairros.isEmpty()) {
            throw new ResourceNotFoundException(
                    "Nenhum bairro encontrado com esse nome"
            );
        }

        return bairros;
    }

    // UPDATE
    public BairroResponse update(
            Integer id,
            BairroRequest request
    ) {

        Bairro bairro = bairroRepository
                .findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Bairro não encontrado"
                        )
                );

        Cidade cidade = cidadeRepository
                .findById(request.cidadeId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Cidade não encontrada"
                        )
                );

        bairro.setNome(request.nome());
        bairro.setCidade(cidade);

        return bairroMapper.bairroToResponse(
                bairroRepository.save(bairro)
        );
    }

    // DELETE
    public void delete(Integer id) {

        Bairro bairro = bairroRepository
                .findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Bairro não encontrado"
                        )
                );

        bairroRepository.delete(bairro);
    }
}