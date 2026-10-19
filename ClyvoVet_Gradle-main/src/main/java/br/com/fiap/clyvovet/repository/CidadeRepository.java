package br.com.fiap.clyvovet.repository;

import br.com.fiap.clyvovet.model.Cidade;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CidadeRepository extends JpaRepository<Cidade, Integer> {
    Page<Cidade> findByNomeContainingIgnoreCase(String nome, Pageable pageable);
}
