package br.com.fiap.clyvovet.dto.response;

import br.com.fiap.clyvovet.model.TipoArquivo;

import java.time.LocalDateTime;

public record AnexoResponse(
        Integer anexoId,
        String nomeArquivo,
        TipoArquivo tipoArquivo,
        String url,
        Double tamanhoKb,
        LocalDateTime dataCriacao,
        Integer usuarioId

) {
}
