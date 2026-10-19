package br.com.fiap.clyvovet.repository;

import br.com.fiap.clyvovet.model.Notificacao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface NotificacaoRepository extends JpaRepository<Notificacao, Long> {

    List<Notificacao> findTop50ByUsuario_IdOrderByDataEnvioDesc(Long usuarioId);

    List<Notificacao> findByUsuario_IdAndDataLeituraIsNull(Long usuarioId);

    long countByUsuario_IdAndDataLeituraIsNull(Long usuarioId);

    Optional<Notificacao> findByIdAndUsuario_Id(Long notificacaoId, Long usuarioId);
}
