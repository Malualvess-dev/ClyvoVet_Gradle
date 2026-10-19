package br.com.fiap.clyvovet.repository;

import br.com.fiap.clyvovet.model.Veterinario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VeterinarioRepository extends JpaRepository<Veterinario, Long> {

    List<Veterinario> findAllByOrderByUsuario_NomeAsc();

    boolean existsByCrmv(String crmv);
}
