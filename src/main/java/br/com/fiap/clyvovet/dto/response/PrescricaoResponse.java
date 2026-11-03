package br.com.fiap.clyvovet.dto.response;

import br.com.fiap.clyvovet.model.TipoPrescricao;

public record PrescricaoResponse(
        TipoPrescricao tipo,
        String descricao,
        String posologia
) {
}
