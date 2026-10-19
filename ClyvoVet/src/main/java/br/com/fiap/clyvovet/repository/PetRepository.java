package br.com.fiap.clyvovet.repository;

import br.com.fiap.clyvovet.model.Pet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PetRepository extends JpaRepository<Pet, Long> {

    List<Pet> findByTutor_Usuario_IdOrderByNomeAsc(Long usuarioId);

    Optional<Pet> findByIdAndTutor_Usuario_Id(Long petId, Long usuarioId);
}
