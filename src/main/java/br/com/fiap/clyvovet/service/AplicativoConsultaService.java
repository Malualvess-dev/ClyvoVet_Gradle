package br.com.fiap.clyvovet.service;

import br.com.fiap.clyvovet.dto.api.AtendimentoApiRequest;
import br.com.fiap.clyvovet.dto.api.ConsultaApiRequest;
import br.com.fiap.clyvovet.dto.api.ConsultaApiResponse;
import br.com.fiap.clyvovet.dto.api.RemarcacaoApiRequest;
import br.com.fiap.clyvovet.dto.api.VeterinarioApiResponse;
import br.com.fiap.clyvovet.dto.request.SolicitacaoConsultaRequest;
import br.com.fiap.clyvovet.mapper.AplicativoMapper;
import br.com.fiap.clyvovet.model.DadosDoAtendimento;
import br.com.fiap.clyvovet.repository.ConsultaRepository;
import br.com.fiap.clyvovet.repository.VeterinarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Consultas pelo aplicativo. As regras (horários, antecedência, status) são as mesmas das telas web,
 * porque tudo passa por AgendamentoService, AtendimentoService e pela entidade Consulta.
 */
@Service
@Transactional
public class AplicativoConsultaService {

    private static final String CANCELADA_PELO_TUTOR = "Cancelada pelo tutor no aplicativo.";
    private static final String DESMARCADA_PELO_VETERINARIO = "Desmarcada pelo veterinário no aplicativo.";
    private static final DateTimeFormatter HORARIO = DateTimeFormatter.ofPattern("HH:mm");

    private final AgendamentoService agendamentoService;
    private final AtendimentoService atendimentoService;
    private final ConsultaService consultaService;
    private final ConsultaRepository consultaRepository;
    private final VeterinarioRepository veterinarioRepository;
    private final AplicativoMapper mapper;

    public AplicativoConsultaService(AgendamentoService agendamentoService, AtendimentoService atendimentoService,
                                     ConsultaService consultaService, ConsultaRepository consultaRepository,
                                     VeterinarioRepository veterinarioRepository, AplicativoMapper mapper) {
        this.agendamentoService = agendamentoService;
        this.atendimentoService = atendimentoService;
        this.consultaService = consultaService;
        this.consultaRepository = consultaRepository;
        this.veterinarioRepository = veterinarioRepository;
        this.mapper = mapper;
    }

    @Transactional(readOnly = true)
    public List<ConsultaApiResponse> listarDoTutor(Long usuarioTutorId) {
        return consultaRepository.findByPet_Tutor_Usuario_IdOrderByDataHoraDesc(usuarioTutorId).stream()
                .map(mapper::consulta).toList();
    }

    @Transactional(readOnly = true)
    public List<ConsultaApiResponse> listarDoVeterinario(Long usuarioVeterinarioId) {
        return consultaRepository.findByVeterinario_Usuario_IdOrderByDataHoraDesc(usuarioVeterinarioId).stream()
                .map(mapper::consulta).toList();
    }

    @Transactional(readOnly = true)
    public List<VeterinarioApiResponse> listarVeterinarios() {
        return veterinarioRepository.findAllByOrderByNomeAsc().stream().map(mapper::veterinario).toList();
    }

    @Transactional(readOnly = true)
    public List<String> horariosDisponiveis(Long veterinarioId, LocalDate data) {
        return agendamentoService.horariosDisponiveis(veterinarioId, data).stream()
                .map(horario -> horario.format(HORARIO))
                .toList();
    }

    public ConsultaApiResponse solicitar(Long usuarioTutorId, ConsultaApiRequest request) {
        SolicitacaoConsultaRequest solicitacao = new SolicitacaoConsultaRequest();
        solicitacao.setPetId(request.petId());
        solicitacao.setVeterinarioId(request.veterinarioId());
        solicitacao.setData(request.data());
        solicitacao.setHorario(request.horario());
        solicitacao.setModalidade(request.modalidadeOuPadrao());
        solicitacao.setMotivo(request.motivo());
        Long consultaId = agendamentoService.solicitar(usuarioTutorId, solicitacao).id();
        return mapper.consulta(consultaService.buscarDoTutor(usuarioTutorId, consultaId));
    }

    public ConsultaApiResponse remarcar(Long usuarioTutorId, Long consultaId, RemarcacaoApiRequest request) {
        agendamentoService.remarcar(usuarioTutorId, consultaId, LocalDateTime.of(request.data(), request.horario()));
        return mapper.consulta(consultaService.buscarDoTutor(usuarioTutorId, consultaId));
    }

    public ConsultaApiResponse cancelarPeloTutor(Long usuarioTutorId, Long consultaId, String justificativa) {
        agendamentoService.cancelar(usuarioTutorId, consultaId, ouPadrao(justificativa, CANCELADA_PELO_TUTOR));
        return mapper.consulta(consultaService.buscarDoTutor(usuarioTutorId, consultaId));
    }

    public ConsultaApiResponse desmarcarPeloVeterinario(Long usuarioVeterinarioId, Long consultaId,
                                                        String justificativa) {
        agendamentoService.desmarcarPeloVeterinario(usuarioVeterinarioId, consultaId,
                ouPadrao(justificativa, DESMARCADA_PELO_VETERINARIO));
        return mapper.consulta(consultaService.buscarDoVeterinario(usuarioVeterinarioId, consultaId));
    }

    public ConsultaApiResponse confirmar(Long usuarioVeterinarioId, Long consultaId) {
        agendamentoService.confirmar(usuarioVeterinarioId, consultaId);
        return mapper.consulta(consultaService.buscarDoVeterinario(usuarioVeterinarioId, consultaId));
    }

    public ConsultaApiResponse finalizar(Long usuarioVeterinarioId, Long consultaId, AtendimentoApiRequest request) {
        DadosDoAtendimento dados = new DadosDoAtendimento(request.observacaoVeterinario(), null, null,
                request.pesoKg(), null, null, null, null);
        atendimentoService.registrarPeloAplicativo(usuarioVeterinarioId, consultaId, dados);
        return mapper.consulta(consultaService.buscarDoVeterinario(usuarioVeterinarioId, consultaId));
    }

    public void excluir(Long usuarioTutorId, Long consultaId) {
        agendamentoService.excluir(usuarioTutorId, consultaId);
    }

    private static String ouPadrao(String justificativa, String padrao) {
        return justificativa == null || justificativa.isBlank() ? padrao : justificativa;
    }
}
