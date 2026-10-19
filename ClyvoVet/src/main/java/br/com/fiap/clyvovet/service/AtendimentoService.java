package br.com.fiap.clyvovet.service;

import br.com.fiap.clyvovet.dto.AtendimentoForm;
import br.com.fiap.clyvovet.model.Consulta;
import br.com.fiap.clyvovet.model.Vacina;
import br.com.fiap.clyvovet.repository.VacinaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDateTime;

@Service
@Transactional
public class AtendimentoService {

    private final ConsultaService consultaService;
    private final VacinaRepository vacinaRepository;
    private final NotificacaoService notificacaoService;
    private final Clock relogio;

    public AtendimentoService(ConsultaService consultaService, VacinaRepository vacinaRepository,
                              NotificacaoService notificacaoService, Clock relogio) {
        this.consultaService = consultaService;
        this.vacinaRepository = vacinaRepository;
        this.notificacaoService = notificacaoService;
        this.relogio = relogio;
    }

    public void registrar(Long usuarioVeterinarioId, Long consultaId, AtendimentoForm form) {
        Consulta consulta = consultaService.buscarDoVeterinario(usuarioVeterinarioId, consultaId);
        consulta.concluir(form.prontuario(), LocalDateTime.now(relogio));
        if (form.aplicouVacina()) {
            vacinaRepository.save(Vacina.aplicadaEm(consulta, form.getVacina(), form.getProximaDose()));
        }
        notificacaoService.notificar(consulta.getPet().getTutor().getUsuario(),
                "O atendimento de %s foi concluído. O prontuário já está disponível."
                        .formatted(consulta.getPet().getNome()));
    }
}
