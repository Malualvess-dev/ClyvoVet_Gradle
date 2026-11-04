package br.com.fiap.clyvovet.repository;

import br.com.fiap.clyvovet.model.HistoricoSaude;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface HistoricoSaudeRepository extends JpaRepository<HistoricoSaude, Long> {

    List<HistoricoSaude> findByPet_IdOrderByDataEventoDesc(Long petId);

    List<HistoricoSaude> findByPet_Tutor_Usuario_IdAndProximaDoseBetweenOrderByProximaDoseAsc(
            Long usuarioId, LocalDate inicio, LocalDate fim);

    boolean existsByPet_Id(Long petId);
}
