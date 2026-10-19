package br.com.fiap.clyvovet.mapper;

import br.com.fiap.clyvovet.dto.response.NotificacaoResponse;
import br.com.fiap.clyvovet.model.Notificacao;
import org.springframework.stereotype.Component;

@Component
public class NotificacaoMapper {

    public NotificacaoResponse paraResponse(Notificacao notificacao) {
        return new NotificacaoResponse(
                notificacao.getId(),
                notificacao.getMensagem(),
                notificacao.getDataEnvio(),
                notificacao.isLida()
        );
    }
}
