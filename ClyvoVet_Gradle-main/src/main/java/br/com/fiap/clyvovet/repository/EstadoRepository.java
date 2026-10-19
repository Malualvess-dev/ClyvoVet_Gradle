package br.com.fiap.clyvovet.repository;

import br.com.fiap.clyvovet.model.Estado;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EstadoRepository extends JpaRepository<Estado, Integer> {
    Optional<Estado> findByUf(String uf);
}
