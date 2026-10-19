package br.com.fiap.clyvovet.repository;

import br.com.fiap.clyvovet.model.Agenda;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;


import java.time.LocalDateTime;

public interface AgendaRepository extends JpaRepository<Agenda, Integer> {
    Page<Agenda> findByDataHoraBetween(LocalDateTime inicio, LocalDateTime fim, Pageable pageable);
}
