package br.com.fiap.clyvovet.mapper;

import br.com.fiap.clyvovet.controller.NotificacaoController;
import br.com.fiap.clyvovet.dto.response.NotificacaoResponse;
import br.com.fiap.clyvovet.model.Notificacao;
import org.springframework.hateoas.Link;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class NotificacaoMapper {

    public NotificacaoResponse notificacaoToResponse(Notificacao notificacao) {

        Link link = linkTo(
                methodOn(NotificacaoController.class)
                        .readNotificacao(notificacao.getNotificacaoId())
        ).withRel("Detalhes da notificação");

        return new NotificacaoResponse(
                notificacao.getNotificacaoId(),
                notificacao.getMensagem(),
                notificacao.getDataEnvio(),
                notificacao.getLida(),
                notificacao.getUsuario().getUsuarioId(),
                link
        );
    }
}
