package br.com.fiap.clyvovet.repository;

import br.com.fiap.clyvovet.model.Consulta;
import br.com.fiap.clyvovet.model.StatusConsulta;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface ConsultaRepository extends JpaRepository<Consulta, Long> {

    Optional<Consulta> findByIdAndVeterinario_Usuario_Id(Long consultaId, Long usuarioId);

    Optional<Consulta> findByIdAndPet_Tutor_Usuario_Id(Long consultaId, Long usuarioId);

    List<Consulta> findByPet_Tutor_Usuario_IdOrderByDataHoraDesc(Long usuarioId);

    List<Consulta> findByPet_Tutor_Usuario_IdAndStatusInAndDataHoraAfterOrderByDataHoraAsc(
            Long usuarioId, Collection<StatusConsulta> status, LocalDateTime aPartirDe);

    List<Consulta> findByVeterinario_Usuario_IdAndStatusOrderByDataHoraAsc(Long usuarioId, StatusConsulta status);

    List<Consulta> findByPet_IdAndStatusOrderByDataHoraDesc(Long petId, StatusConsulta status);

    List<Consulta> findByVeterinario_IdAndStatusInAndDataHoraBetween(
            Long veterinarioId, Collection<StatusConsulta> status, LocalDateTime inicio, LocalDateTime fim);

    boolean existsByPet_IdAndStatusInAndDataHoraBetween(
            Long petId, Collection<StatusConsulta> status, LocalDateTime inicio, LocalDateTime fim);
}
