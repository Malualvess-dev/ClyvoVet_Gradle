package br.com.fiap.clyvovet.repository;

import br.com.fiap.clyvovet.model.Cidade;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CidadeRepository extends JpaRepository<Cidade, Integer> {
}
