package br.com.fiap.clyvovet.repository;

import br.com.fiap.clyvovet.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {
}
