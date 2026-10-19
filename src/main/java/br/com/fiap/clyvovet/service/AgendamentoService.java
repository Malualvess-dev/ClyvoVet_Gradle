package br.com.fiap.clyvovet.service;

import br.com.fiap.clyvovet.dto.request.SolicitacaoConsultaRequest;
import br.com.fiap.clyvovet.dto.response.ConsultaResponse;
import br.com.fiap.clyvovet.exception.RegraNegocioException;
import br.com.fiap.clyvovet.mapper.ConsultaMapper;
import br.com.fiap.clyvovet.model.Consulta;
import br.com.fiap.clyvovet.model.Pet;
import br.com.fiap.clyvovet.model.StatusConsulta;
import br.com.fiap.clyvovet.model.Veterinario;
import br.com.fiap.clyvovet.repository.ConsultaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class AgendamentoService {

    private static final DateTimeFormatter DATA_HORA = DateTimeFormatter.ofPattern("dd/MM/yyyy 'às' HH:mm");

    private final ConsultaRepository consultaRepository;
    private final ConsultaService consultaService;
    private final PetService petService;
    private final VeterinarioService veterinarioService;
    private final PoliticaDeAgendamento politica;
    private final NotificacaoService notificacaoService;
    private final ConsultaMapper consultaMapper;
    private final Clock relogio;

    public AgendamentoService(ConsultaRepository consultaRepository, ConsultaService consultaService,
                              PetService petService, VeterinarioService veterinarioService,
                              PoliticaDeAgendamento politica, NotificacaoService notificacaoService,
                              ConsultaMapper consultaMapper, Clock relogio) {
        this.consultaRepository = consultaRepository;
        this.consultaService = consultaService;
        this.petService = petService;
        this.veterinarioService = veterinarioService;
        this.politica = politica;
        this.notificacaoService = notificacaoService;
        this.consultaMapper = consultaMapper;
        this.relogio = relogio;
    }

    @Transactional(readOnly = true)
    public List<LocalTime> horariosDisponiveis(Long veterinarioId, LocalDate data) {
        Set<LocalTime> ocupados = consultaRepository
                .findByVeterinario_IdAndStatusInAndDataHoraBetween(veterinarioId, StatusConsulta.queOcupamAgenda(),
                        data.atStartOfDay(), data.plusDays(1).atStartOfDay())
                .stream()
                .map(consulta -> consulta.getDataHora().toLocalTime())
                .collect(Collectors.toSet());
        return politica.horariosDeAtendimento(data).stream()
                .filter(horario -> !ocupados.contains(horario))
                .toList();
    }

    public ConsultaResponse solicitar(Long usuarioTutorId, SolicitacaoConsultaRequest request) {
        Pet pet = petService.buscarDoTutor(usuarioTutorId, request.getPetId());
        Veterinario veterinario = veterinarioService.buscar(request.getVeterinarioId());
        LocalDateTime dataHora = request.dataHora();

        politica.validar(dataHora);
        garantirHorarioLivre(veterinario, dataHora);
        garantirPetSemConsultaNoDia(pet, dataHora.toLocalDate());

        Consulta consulta = consultaRepository.save(
                Consulta.solicitar(pet, veterinario, dataHora, request.getMotivo(), LocalDateTime.now(relogio)));
        notificacaoService.notificar(veterinario.getUsuario(),
                "Nova solicitação: %s (%s) em %s.".formatted(pet.getNome(), pet.getTutor().getNome(),
                        dataHora.format(DATA_HORA)));
        return consultaMapper.paraResponse(consulta);
    }

    public void confirmar(Long usuarioVeterinarioId, Long consultaId) {
        Consulta consulta = consultaService.buscarDoVeterinario(usuarioVeterinarioId, consultaId);
        consulta.confirmar();
        notificarTutor(consulta, "Consulta de %s confirmada para %s com %s.".formatted(
                consulta.getPet().getNome(), consulta.getDataHora().format(DATA_HORA),
                consulta.getVeterinario().getNome()));
    }

    public void recusar(Long usuarioVeterinarioId, Long consultaId, String justificativa) {
        Consulta consulta = consultaService.buscarDoVeterinario(usuarioVeterinarioId, consultaId);
        consulta.recusar(justificativa);
        notificarTutor(consulta, "A consulta de %s em %s foi recusada: %s".formatted(
                consulta.getPet().getNome(), consulta.getDataHora().format(DATA_HORA), consulta.getJustificativa()));
    }

    public void cancelar(Long usuarioTutorId, Long consultaId, String justificativa) {
        Consulta consulta = consultaService.buscarDoTutor(usuarioTutorId, consultaId);
        consulta.cancelar(justificativa, LocalDateTime.now(relogio));
        notificacaoService.notificar(consulta.getVeterinario().getUsuario(),
                "%s cancelou a consulta de %s em %s.".formatted(consulta.getPet().getTutor().getNome(),
                        consulta.getPet().getNome(), consulta.getDataHora().format(DATA_HORA)));
    }

    private void garantirHorarioLivre(Veterinario veterinario, LocalDateTime dataHora) {
        if (!horariosDisponiveis(veterinario.getId(), dataHora.toLocalDate()).contains(dataHora.toLocalTime())) {
            throw new RegraNegocioException("Este horário acabou de ser reservado. Escolha outro horário.");
        }
    }

    private void garantirPetSemConsultaNoDia(Pet pet, LocalDate data) {
        boolean jaPossuiConsulta = consultaRepository.existsByPet_IdAndStatusInAndDataHoraBetween(pet.getId(),
                StatusConsulta.queOcupamAgenda(), data.atStartOfDay(), data.plusDays(1).atStartOfDay());
        if (jaPossuiConsulta) {
            throw new RegraNegocioException(pet.getNome() + " já possui uma consulta ativa neste dia.");
        }
    }

    private void notificarTutor(Consulta consulta, String mensagem) {
        notificacaoService.notificar(consulta.getPet().getTutor().getUsuario(), mensagem);
    }
}
