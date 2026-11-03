package br.com.fiap.clyvovet.model;

import java.math.BigDecimal;
import java.time.LocalDate;

/** O que o veterinário registra ao concluir uma consulta. A prescrição é opcional (nula). */
public record DadosDoAtendimento(
        String diagnostico,
        String sintomas,
        String tratamento,
        BigDecimal pesoKg,
        BigDecimal temperatura,
        String examesSolicitados,
        LocalDate dataRetorno,
        DadosDaPrescricao prescricao
) {

    public record DadosDaPrescricao(TipoPrescricao tipo, String descricao, String posologia) {
    }
}
