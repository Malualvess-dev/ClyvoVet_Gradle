package br.com.fiap.clyvovet.repository;

import br.com.fiap.clyvovet.model.Agenda;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AgendaRepository extends JpaRepository<Agenda, Integer> {
}
