package br.com.fiap.clyvovet.service;

import br.com.fiap.clyvovet.exception.RecursoNaoEncontradoException;
import br.com.fiap.clyvovet.model.Notificacao;
import br.com.fiap.clyvovet.model.Usuario;
import br.com.fiap.clyvovet.repository.NotificacaoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class NotificacaoService {

    private final NotificacaoRepository notificacaoRepository;
    private final Clock relogio;

    public NotificacaoService(NotificacaoRepository notificacaoRepository, Clock relogio) {
        this.notificacaoRepository = notificacaoRepository;
        this.relogio = relogio;
    }

    public void notificar(Usuario destinatario, String mensagem) {
        notificacaoRepository.save(new Notificacao(destinatario, mensagem, LocalDateTime.now(relogio)));
    }

    @Transactional(readOnly = true)
    public List<Notificacao> listar(Long usuarioId) {
        return notificacaoRepository.findTop50ByUsuario_IdOrderByDataEnvioDesc(usuarioId);
    }

    @Transactional(readOnly = true)
    public long contarNaoLidas(Long usuarioId) {
        return notificacaoRepository.countByUsuario_IdAndDataLeituraIsNull(usuarioId);
    }

    public void marcarComoLida(Long usuarioId, Long notificacaoId) {
        notificacaoRepository.findByIdAndUsuario_Id(notificacaoId, usuarioId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Notificação não encontrada."))
                .marcarComoLida(LocalDateTime.now(relogio));
    }

    public void marcarTodasComoLidas(Long usuarioId) {
        LocalDateTime agora = LocalDateTime.now(relogio);
        notificacaoRepository.findByUsuario_IdAndDataLeituraIsNull(usuarioId)
                .forEach(notificacao -> notificacao.marcarComoLida(agora));
    }
}
