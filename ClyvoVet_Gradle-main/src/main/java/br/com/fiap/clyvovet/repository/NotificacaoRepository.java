package br.com.fiap.clyvovet.repository;

import br.com.fiap.clyvovet.model.Notificacao;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificacaoRepository extends JpaRepository<Notificacao, Integer> {
    Page<Notificacao> findByLida(String lida, Pageable pageable);
}
