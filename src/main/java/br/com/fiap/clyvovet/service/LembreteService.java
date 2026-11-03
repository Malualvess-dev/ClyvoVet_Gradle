package br.com.fiap.clyvovet.service;

import br.com.fiap.clyvovet.dto.request.LembreteRequest;
import br.com.fiap.clyvovet.dto.response.LembreteResponse;
import br.com.fiap.clyvovet.exception.RecursoNaoEncontradoException;
import br.com.fiap.clyvovet.mapper.ContaMapper;
import br.com.fiap.clyvovet.model.Lembrete;
import br.com.fiap.clyvovet.repository.LembreteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/** Agenda pessoal do usuário (TB_AGENDA): cadastro, consulta, alteração e exclusão. */
@Service
@Transactional
public class LembreteService {

    private final LembreteRepository lembreteRepository;
    private final ContaService contaService;
    private final ContaMapper contaMapper;

    public LembreteService(LembreteRepository lembreteRepository, ContaService contaService, ContaMapper contaMapper) {
        this.lembreteRepository = lembreteRepository;
        this.contaService = contaService;
        this.contaMapper = contaMapper;
    }

    @Transactional(readOnly = true)
    public List<LembreteResponse> listar(Long usuarioId) {
        return lembreteRepository.findByUsuario_IdOrderByDataHoraAsc(usuarioId).stream()
                .map(contaMapper::paraResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public LembreteResponse detalhar(Long usuarioId, Long lembreteId) {
        return contaMapper.paraResponse(buscar(usuarioId, lembreteId));
    }

    public LembreteResponse criar(Long usuarioId, LembreteRequest request) {
        Lembrete lembrete = new Lembrete(contaService.buscar(usuarioId), request.getTitulo(), request.getDataHora(),
                request.getAnotacao());
        return contaMapper.paraResponse(lembreteRepository.save(lembrete));
    }

    public LembreteResponse atualizar(Long usuarioId, Long lembreteId, LembreteRequest request) {
        Lembrete lembrete = buscar(usuarioId, lembreteId);
        lembrete.atualizar(request.getTitulo(), request.getDataHora(), request.getAnotacao());
        return contaMapper.paraResponse(lembrete);
    }

    public void excluir(Long usuarioId, Long lembreteId) {
        lembreteRepository.delete(buscar(usuarioId, lembreteId));
    }

    private Lembrete buscar(Long usuarioId, Long lembreteId) {
        return lembreteRepository.findByIdAndUsuario_Id(lembreteId, usuarioId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Lembrete não encontrado."));
    }
}
