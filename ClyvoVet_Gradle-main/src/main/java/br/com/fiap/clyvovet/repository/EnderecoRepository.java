package br.com.fiap.clyvovet.repository;

import br.com.fiap.clyvovet.model.Endereco;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EnderecoRepository extends JpaRepository<Endereco, Integer> {
    Page<Endereco> findByCep(String cep, Pageable pageable);
}
