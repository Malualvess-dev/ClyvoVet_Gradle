package br.com.fiap.clyvovet.dto.response;

import java.time.LocalDateTime;

public record NotificacaoResponse(
        Integer notificacaoId,
        String mensagem,
        LocalDateTime dataEnvio,
        String lida,
        Integer usuarioId
) {
}
