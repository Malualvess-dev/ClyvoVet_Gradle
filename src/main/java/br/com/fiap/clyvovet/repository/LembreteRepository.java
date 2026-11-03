package br.com.fiap.clyvovet.repository;

import br.com.fiap.clyvovet.model.Lembrete;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LembreteRepository extends JpaRepository<Lembrete, Long> {

    List<Lembrete> findByUsuario_IdOrderByDataHoraAsc(Long usuarioId);

    Optional<Lembrete> findByIdAndUsuario_Id(Long lembreteId, Long usuarioId);
}
