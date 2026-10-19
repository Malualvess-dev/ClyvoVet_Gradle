package br.com.fiap.clyvovet.repository;

import br.com.fiap.clyvovet.model.Bairro;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BairroRepository extends JpaRepository<Bairro, Integer> {
    Page<Bairro> findByNomeContainingIgnoreCase(String nome, Pageable pageable);
}
