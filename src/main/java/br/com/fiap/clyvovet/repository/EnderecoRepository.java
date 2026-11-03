package br.com.fiap.clyvovet.repository;

import br.com.fiap.clyvovet.model.Endereco;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EnderecoRepository extends JpaRepository<Endereco, Long> {

    Optional<Endereco> findFirstByUsuario_IdOrderByIdAsc(Long usuarioId);
}
