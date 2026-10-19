package br.com.fiap.clyvovet.service;

import br.com.fiap.clyvovet.dto.response.VacinaResponse;
import br.com.fiap.clyvovet.mapper.VacinaMapper;
import br.com.fiap.clyvovet.model.Vacina;
import br.com.fiap.clyvovet.repository.VacinaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDate;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class VacinaService {

    static final int DIAS_DE_ANTECEDENCIA_DO_LEMBRETE = 30;

    private final VacinaRepository vacinaRepository;
    private final VacinaMapper vacinaMapper;
    private final Clock relogio;

    public VacinaService(VacinaRepository vacinaRepository, VacinaMapper vacinaMapper, Clock relogio) {
        this.vacinaRepository = vacinaRepository;
        this.vacinaMapper = vacinaMapper;
        this.relogio = relogio;
    }

    public List<VacinaResponse> carteiraDoPet(Long petId) {
        return paraResponse(vacinaRepository.findByPet_IdOrderByDataAplicacaoDesc(petId));
    }

    public List<VacinaResponse> reforcosProximos(Long usuarioTutorId) {
        LocalDate hoje = LocalDate.now(relogio);
        return paraResponse(vacinaRepository.findByPet_Tutor_Usuario_IdAndProximaDoseBetweenOrderByProximaDoseAsc(
                usuarioTutorId, hoje, hoje.plusDays(DIAS_DE_ANTECEDENCIA_DO_LEMBRETE)));
    }

    private List<VacinaResponse> paraResponse(List<Vacina> vacinas) {
        return vacinas.stream().map(vacinaMapper::paraResponse).toList();
    }
}
