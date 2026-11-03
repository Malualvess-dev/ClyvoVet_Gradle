package br.com.fiap.clyvovet.service;

import br.com.fiap.clyvovet.dto.request.AnexoRequest;
import br.com.fiap.clyvovet.dto.response.AnexoResponse;
import br.com.fiap.clyvovet.exception.RecursoNaoEncontradoException;
import br.com.fiap.clyvovet.mapper.ContaMapper;
import br.com.fiap.clyvovet.model.Anexo;
import br.com.fiap.clyvovet.repository.AnexoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class AnexoService {

    private final AnexoRepository anexoRepository;
    private final ContaService contaService;
    private final ContaMapper contaMapper;
    private final Clock relogio;

    public AnexoService(AnexoRepository anexoRepository, ContaService contaService, ContaMapper contaMapper,
                        Clock relogio) {
        this.anexoRepository = anexoRepository;
        this.contaService = contaService;
        this.contaMapper = contaMapper;
        this.relogio = relogio;
    }

    @Transactional(readOnly = true)
    public List<AnexoResponse> listar(Long usuarioId) {
        return anexoRepository.findByUsuario_IdOrderByDataCriacaoDesc(usuarioId).stream()
                .map(contaMapper::paraResponse)
                .toList();
    }

    public AnexoResponse adicionar(Long usuarioId, AnexoRequest request) {
        Anexo anexo = new Anexo(contaService.buscar(usuarioId), request.getNomeArquivo(), request.getTipoArquivo(),
                request.getUrl(), request.getTamanhoKb(), LocalDateTime.now(relogio));
        return contaMapper.paraResponse(anexoRepository.save(anexo));
    }

    public void excluir(Long usuarioId, Long anexoId) {
        Anexo anexo = anexoRepository.findByIdAndUsuario_Id(anexoId, usuarioId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Documento não encontrado."));
        anexoRepository.delete(anexo);
    }
}
