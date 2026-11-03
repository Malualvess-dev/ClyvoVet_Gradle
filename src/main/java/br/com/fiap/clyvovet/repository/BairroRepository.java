package br.com.fiap.clyvovet.repository;

import br.com.fiap.clyvovet.model.Bairro;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BairroRepository extends JpaRepository<Bairro, Long> {

    List<Bairro> findAllByOrderByCidade_Estado_UfAscCidade_NomeAscNomeAsc();
}
