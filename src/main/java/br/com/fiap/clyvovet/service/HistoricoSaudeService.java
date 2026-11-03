package br.com.fiap.clyvovet.service;

import br.com.fiap.clyvovet.dto.response.HistoricoSaudeResponse;
import br.com.fiap.clyvovet.mapper.HistoricoSaudeMapper;
import br.com.fiap.clyvovet.model.HistoricoSaude;
import br.com.fiap.clyvovet.repository.HistoricoSaudeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDate;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class HistoricoSaudeService {

    static final int DIAS_DE_ANTECEDENCIA_DO_LEMBRETE = 30;

    private final HistoricoSaudeRepository historicoSaudeRepository;
    private final HistoricoSaudeMapper historicoSaudeMapper;
    private final Clock relogio;

    public HistoricoSaudeService(HistoricoSaudeRepository historicoSaudeRepository,
                                 HistoricoSaudeMapper historicoSaudeMapper, Clock relogio) {
        this.historicoSaudeRepository = historicoSaudeRepository;
        this.historicoSaudeMapper = historicoSaudeMapper;
        this.relogio = relogio;
    }

    public List<HistoricoSaudeResponse> doPet(Long petId) {
        return paraResponse(historicoSaudeRepository.findByPet_IdOrderByDataEventoDesc(petId));
    }

    public List<HistoricoSaudeResponse> reforcosProximos(Long usuarioTutorId) {
        LocalDate hoje = LocalDate.now(relogio);
        return paraResponse(historicoSaudeRepository.findByPet_Tutor_Usuario_IdAndProximaDoseBetweenOrderByProximaDoseAsc(
                usuarioTutorId, hoje, hoje.plusDays(DIAS_DE_ANTECEDENCIA_DO_LEMBRETE)));
    }

    private List<HistoricoSaudeResponse> paraResponse(List<HistoricoSaude> eventos) {
        return eventos.stream().map(historicoSaudeMapper::paraResponse).toList();
    }
}
