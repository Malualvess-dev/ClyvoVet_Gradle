package br.com.fiap.clyvovet.service;

import br.com.fiap.clyvovet.exception.RecursoNaoEncontradoException;
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
    private final Clock relogio;

    public ConsultaService(ConsultaRepository consultaRepository, Clock relogio) {
        this.consultaRepository = consultaRepository;
        this.relogio = relogio;
    }

    public Consulta buscarDoVeterinario(Long usuarioVeterinarioId, Long consultaId) {
        return consultaRepository.findByIdAndVeterinario_Usuario_Id(consultaId, usuarioVeterinarioId)
                .orElseThrow(ConsultaService::consultaNaoEncontrada);
    }

    public Consulta buscarDoTutor(Long usuarioTutorId, Long consultaId) {
        return consultaRepository.findByIdAndPet_Tutor_Usuario_Id(consultaId, usuarioTutorId)
                .orElseThrow(ConsultaService::consultaNaoEncontrada);
    }

    public List<Consulta> listarDoTutor(Long usuarioTutorId) {
        return consultaRepository.findByPet_Tutor_Usuario_IdOrderByDataHoraDesc(usuarioTutorId);
    }

    public List<Consulta> proximasDoTutor(Long usuarioTutorId) {
        return consultaRepository.findByPet_Tutor_Usuario_IdAndStatusInAndDataHoraAfterOrderByDataHoraAsc(
                usuarioTutorId, StatusConsulta.queOcupamAgenda(), LocalDateTime.now(relogio));
    }

    public List<Consulta> listarDoVeterinarioPorStatus(Long usuarioVeterinarioId, StatusConsulta status) {
        return consultaRepository.findByVeterinario_Usuario_IdAndStatusOrderByDataHoraAsc(
                usuarioVeterinarioId, status);
    }

    public List<Consulta> historicoClinico(Long petId) {
        return consultaRepository.findByPet_IdAndStatusOrderByDataHoraDesc(petId, StatusConsulta.CONCLUIDA);
    }

    private static RecursoNaoEncontradoException consultaNaoEncontrada() {
        return new RecursoNaoEncontradoException("Consulta não encontrada.");
    }
}
