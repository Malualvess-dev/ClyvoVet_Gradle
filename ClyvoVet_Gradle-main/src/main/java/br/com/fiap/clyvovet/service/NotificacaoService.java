package br.com.fiap.clyvovet.service;

import br.com.fiap.clyvovet.dto.request.NotificacaoRequest;
import br.com.fiap.clyvovet.dto.response.NotificacaoResponse;
import br.com.fiap.clyvovet.exception.ResourceNotFoundException;
import br.com.fiap.clyvovet.mapper.NotificacaoMapper;
import br.com.fiap.clyvovet.model.Notificacao;
import br.com.fiap.clyvovet.model.Usuario;
import br.com.fiap.clyvovet.repository.NotificacaoRepository;
import br.com.fiap.clyvovet.repository.UsuarioRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class NotificacaoService {

    private final NotificacaoRepository notificacaoRepository;
    private final UsuarioRepository usuarioRepository;
    private final NotificacaoMapper notificacaoMapper;

    public NotificacaoService(
            NotificacaoRepository notificacaoRepository,
            UsuarioRepository usuarioRepository,
            NotificacaoMapper notificacaoMapper
    ) {
        this.notificacaoRepository = notificacaoRepository;
        this.usuarioRepository = usuarioRepository;
        this.notificacaoMapper = notificacaoMapper;
    }

    // CREATE
    public NotificacaoResponse create(NotificacaoRequest request) {

        Usuario usuario = usuarioRepository
                .findById(request.usuarioId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Usuário não encontrado"
                        )
                );

        Notificacao notificacao = new Notificacao();

        notificacao.setMensagem(request.mensagem());

        notificacao.setDataEnvio(
                request.dataEnvio() != null
                        ? request.dataEnvio()
                        : LocalDateTime.now()
        );

        notificacao.setLida(request.lida());
        notificacao.setUsuario(usuario);

        return notificacaoMapper.notificacaoToResponse(
                notificacaoRepository.save(notificacao)
        );
    }

    // READ POR ID
    public NotificacaoResponse readNotificacao(Integer id) {

        Notificacao notificacao = notificacaoRepository
                .findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Notificação não encontrada"
                        )
                );

        return notificacaoMapper.notificacaoToResponse(notificacao);
    }

    // READ TODOS
    public Page<NotificacaoResponse> read(Pageable pageable) {

        Pageable pageableSemOrdenacao =
                PageRequest.of(
                        pageable.getPageNumber(),
                        pageable.getPageSize()
                );

        Page<NotificacaoResponse> notificacoes =
                notificacaoRepository
                        .findAll(pageableSemOrdenacao)
                        .map(notificacaoMapper::notificacaoToResponse);

        if (notificacoes.isEmpty()) {
            throw new ResourceNotFoundException(
                    "Não possui nenhuma notificação cadastrada"
            );
        }

        return notificacoes;
    }

    // READ POR STATUS DE LEITURA
    public Page<NotificacaoResponse> readByLida(
            String lida,
            Pageable pageable
    ) {

        Pageable pageableSemOrdenacao =
                PageRequest.of(
                        pageable.getPageNumber(),
                        pageable.getPageSize()
                );

        Page<NotificacaoResponse> notificacoes =
                notificacaoRepository
                        .findByLida(
                                lida,
                                pageableSemOrdenacao
                        )
                        .map(notificacaoMapper::notificacaoToResponse);

        if (notificacoes.isEmpty()) {
            throw new ResourceNotFoundException(
                    "Nenhuma notificação encontrada com esse status de leitura"
            );
        }

        return notificacoes;
    }

    // UPDATE
    public NotificacaoResponse update(
            Integer id,
            NotificacaoRequest request
    ) {

        Notificacao notificacao = notificacaoRepository
                .findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Notificação não encontrada"
                        )
                );

        Usuario usuario = usuarioRepository
                .findById(request.usuarioId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Usuário não encontrado"
                        )
                );

        notificacao.setMensagem(request.mensagem());

        notificacao.setDataEnvio(
                request.dataEnvio() != null
                        ? request.dataEnvio()
                        : notificacao.getDataEnvio()
        );

        notificacao.setLida(request.lida());
        notificacao.setUsuario(usuario);

        return notificacaoMapper.notificacaoToResponse(
                notificacaoRepository.save(notificacao)
        );
    }

    // DELETE
    public void delete(Integer id) {

        Notificacao notificacao = notificacaoRepository
                .findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Notificação não encontrada"
                        )
                );

        notificacaoRepository.delete(notificacao);
    }
}