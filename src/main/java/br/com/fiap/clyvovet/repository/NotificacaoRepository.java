package br.com.fiap.clyvovet.repository;

import br.com.fiap.clyvovet.model.Notificacao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface NotificacaoRepository extends JpaRepository<Notificacao, Long> {

    List<Notificacao> findTop50ByUsuario_IdOrderByDataEnvioDesc(Long usuarioId);

    // O status "lida" é recebido como parâmetro para passar pelo conversor S/N da coluna LIDA.
    List<Notificacao> findByUsuario_IdAndLida(Long usuarioId, boolean lida);

    long countByUsuario_IdAndLida(Long usuarioId, boolean lida);

    Optional<Notificacao> findByIdAndUsuario_Id(Long notificacaoId, Long usuarioId);
}
