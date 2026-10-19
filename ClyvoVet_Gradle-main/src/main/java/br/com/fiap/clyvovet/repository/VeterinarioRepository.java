package br.com.fiap.clyvovet.repository;

import br.com.fiap.clyvovet.model.Veterinario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VeterinarioRepository
        extends JpaRepository<Veterinario, Integer> {

    Optional<Veterinario> findByCrmv(String crmv);
}