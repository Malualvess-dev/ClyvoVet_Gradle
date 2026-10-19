package br.com.fiap.clyvovet.service;

import br.com.fiap.clyvovet.dto.response.ConsultaResponse;
import br.com.fiap.clyvovet.exception.RecursoNaoEncontradoException;
import br.com.fiap.clyvovet.mapper.ConsultaMapper;
import br.com.fiap.clyvovet.model.Consulta;
import br.com.fiap.clyvovet.model.StatusConsulta;
import br.com.fiap.clyvovet.repository.ConsultaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class ConsultaService {

    private final ConsultaRepository consultaRepository;
    private final ConsultaMapper consultaMapper;
    private final Clock relogio;

    public ConsultaService(ConsultaRepository consultaRepository, ConsultaMapper consultaMapper, Clock relogio) {
        this.consultaRepository = consultaRepository;
        this.consultaMapper = consultaMapper;
        this.relogio = relogio;
    }

    public ConsultaResponse detalharDoVeterinario(Long usuarioVeterinarioId, Long consultaId) {
        return consultaMapper.paraResponse(buscarDoVeterinario(usuarioVeterinarioId, consultaId));
    }

    public List<ConsultaResponse> listarDoTutor(Long usuarioTutorId) {
        return paraResponse(consultaRepository.findByPet_Tutor_Usuario_IdOrderByDataHoraDesc(usuarioTutorId));
    }

    public List<ConsultaResponse> proximasDoTutor(Long usuarioTutorId) {
        return paraResponse(consultaRepository.findByPet_Tutor_Usuario_IdAndStatusInAndDataHoraAfterOrderByDataHoraAsc(
                usuarioTutorId, StatusConsulta.queOcupamAgenda(), LocalDateTime.now(relogio)));
    }

    public List<ConsultaResponse> listarDoVeterinarioPorStatus(Long usuarioVeterinarioId, StatusConsulta status) {
        return paraResponse(consultaRepository.findByVeterinario_Usuario_IdAndStatusOrderByDataHoraAsc(
                usuarioVeterinarioId, status));
    }

    public List<ConsultaResponse> historicoClinico(Long petId) {
        return paraResponse(consultaRepository.findByPet_IdAndStatusOrderByDataHoraDesc(
                petId, StatusConsulta.CONCLUIDA));
    }

    Consulta buscarDoVeterinario(Long usuarioVeterinarioId, Long consultaId) {
        return consultaRepository.findByIdAndVeterinario_Usuario_Id(consultaId, usuarioVeterinarioId)
                .orElseThrow(ConsultaService::consultaNaoEncontrada);
    }

    Consulta buscarDoTutor(Long usuarioTutorId, Long consultaId) {
        return consultaRepository.findByIdAndPet_Tutor_Usuario_Id(consultaId, usuarioTutorId)
                .orElseThrow(ConsultaService::consultaNaoEncontrada);
    }

    private List<ConsultaResponse> paraResponse(List<Consulta> consultas) {
        return consultas.stream().map(consultaMapper::paraResponse).toList();
    }

    private static RecursoNaoEncontradoException consultaNaoEncontrada() {
        return new RecursoNaoEncontradoException("Consulta não encontrada.");
    }
}
