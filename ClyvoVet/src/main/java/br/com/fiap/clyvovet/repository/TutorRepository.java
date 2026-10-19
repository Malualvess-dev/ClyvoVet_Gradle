package br.com.fiap.clyvovet.repository;

import br.com.fiap.clyvovet.model.Tutor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TutorRepository extends JpaRepository<Tutor, Long> {

    Optional<Tutor> findByUsuario_Id(Long usuarioId);

    boolean existsByCpf(String cpf);
}
