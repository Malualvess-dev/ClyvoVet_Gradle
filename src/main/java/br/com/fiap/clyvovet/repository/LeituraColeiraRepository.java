package br.com.fiap.clyvovet.repository;

import br.com.fiap.clyvovet.model.LeituraColeira;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LeituraColeiraRepository extends JpaRepository<LeituraColeira, Long> {

    List<LeituraColeira> findTop10ByPet_IdOrderByDataHoraDesc(Long petId);

    boolean existsByPet_Id(Long petId);
}
