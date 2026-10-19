package br.com.fiap.clyvovet.dto.response;

import java.time.LocalDateTime;

public record NotificacaoResponse(
        Long id,
        String mensagem,
        LocalDateTime dataEnvio,
        boolean lida
) {
}
