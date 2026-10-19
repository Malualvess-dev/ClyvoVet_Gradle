package br.com.fiap.clyvovet.repository;

import br.com.fiap.clyvovet.model.Vacina;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface VacinaRepository extends JpaRepository<Vacina, Long> {

    List<Vacina> findByPet_IdOrderByDataAplicacaoDesc(Long petId);

    List<Vacina> findByPet_Tutor_Usuario_IdAndProximaDoseBetweenOrderByProximaDoseAsc(
            Long usuarioId, LocalDate inicio, LocalDate fim);
}
