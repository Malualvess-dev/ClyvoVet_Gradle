package br.com.fiap.clyvovet.service;

import br.com.fiap.clyvovet.dto.request.EnderecoRequest;
import br.com.fiap.clyvovet.dto.response.BairroResponse;
import br.com.fiap.clyvovet.dto.response.EnderecoResponse;
import br.com.fiap.clyvovet.exception.RecursoNaoEncontradoException;
import br.com.fiap.clyvovet.mapper.ContaMapper;
import br.com.fiap.clyvovet.model.Bairro;
import br.com.fiap.clyvovet.model.Endereco;
import br.com.fiap.clyvovet.repository.BairroRepository;
import br.com.fiap.clyvovet.repository.EnderecoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class EnderecoService {

    private final EnderecoRepository enderecoRepository;
    private final BairroRepository bairroRepository;
    private final ContaService contaService;
    private final ContaMapper contaMapper;

    public EnderecoService(EnderecoRepository enderecoRepository, BairroRepository bairroRepository,
                           ContaService contaService, ContaMapper contaMapper) {
        this.enderecoRepository = enderecoRepository;
        this.bairroRepository = bairroRepository;
        this.contaService = contaService;
        this.contaMapper = contaMapper;
    }

    @Transactional(readOnly = true)
    public Optional<EnderecoResponse> buscar(Long usuarioId) {
        return enderecoRepository.findFirstByUsuario_IdOrderByIdAsc(usuarioId).map(contaMapper::paraResponse);
    }

    @Transactional(readOnly = true)
    public List<BairroResponse> listarBairros() {
        return bairroRepository.findAllByOrderByCidade_Estado_UfAscCidade_NomeAscNomeAsc().stream()
                .map(contaMapper::paraResponse)
                .toList();
    }

    /** Cria o endereço na primeira vez e atualiza o mesmo registro nas próximas. */
    public EnderecoResponse salvar(Long usuarioId, EnderecoRequest request) {
        Bairro bairro = bairroRepository.findById(request.getBairroId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Bairro não encontrado."));
        Endereco endereco = enderecoRepository.findFirstByUsuario_IdOrderByIdAsc(usuarioId)
                .map(existente -> {
                    existente.atualizar(bairro, request.getLogradouro(), request.getNumero(),
                            request.getComplemento(), request.getCep());
                    return existente;
                })
                .orElseGet(() -> enderecoRepository.save(new Endereco(contaService.buscar(usuarioId), bairro,
                        request.getLogradouro(), request.getNumero(), request.getComplemento(), request.getCep())));
        return contaMapper.paraResponse(endereco);
    }
}
