package br.com.fiap.clyvovet.service;

import br.com.fiap.clyvovet.dto.request.AtendimentoRequest;
import br.com.fiap.clyvovet.model.Consulta;
import br.com.fiap.clyvovet.model.HistoricoSaude;
import br.com.fiap.clyvovet.repository.HistoricoSaudeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDateTime;

@Service
@Transactional
public class AtendimentoService {

    private final ConsultaService consultaService;
    private final HistoricoSaudeRepository historicoSaudeRepository;
    private final NotificacaoService notificacaoService;
    private final Clock relogio;

    public AtendimentoService(ConsultaService consultaService, HistoricoSaudeRepository historicoSaudeRepository,
                              NotificacaoService notificacaoService, Clock relogio) {
        this.consultaService = consultaService;
        this.historicoSaudeRepository = historicoSaudeRepository;
        this.notificacaoService = notificacaoService;
        this.relogio = relogio;
    }

    public void registrar(Long usuarioVeterinarioId, Long consultaId, AtendimentoRequest request) {
        Consulta consulta = consultaService.buscarDoVeterinario(usuarioVeterinarioId, consultaId);
        LocalDateTime agora = LocalDateTime.now(relogio);
        consulta.concluir(request.dadosDoAtendimento(), agora);
        if (request.aplicouVacina()) {
            historicoSaudeRepository.save(HistoricoSaude.vacinaAplicada(
                    consulta, request.getVacina(), agora.toLocalDate(), request.getProximaDose()));
        }
        notificacaoService.notificar(consulta.getPet().getTutor().getUsuario(),
                "O atendimento de %s foi concluído. O prontuário já está disponível."
                        .formatted(consulta.getPet().getNome()));
    }
}
