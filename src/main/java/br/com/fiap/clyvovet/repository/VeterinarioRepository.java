package br.com.fiap.clyvovet.repository;

import br.com.fiap.clyvovet.model.Veterinario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface VeterinarioRepository extends JpaRepository<Veterinario, Long> {

    List<Veterinario> findAllByOrderByNomeAsc();

    Optional<Veterinario> findByUsuario_Id(Long usuarioId);

    boolean existsByCrmv(String crmv);
}
