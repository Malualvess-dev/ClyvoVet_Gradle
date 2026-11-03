package br.com.fiap.clyvovet.dto.response;

import java.time.LocalDateTime;

public record AnexoResponse(
        Long id,
        String nomeArquivo,
        String tipoArquivo,
        String url,
        Integer tamanhoKb,
        LocalDateTime dataCriacao
) {
}
