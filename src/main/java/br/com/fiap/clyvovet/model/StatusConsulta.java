package br.com.fiap.clyvovet.model;

import java.util.Arrays;
import java.util.Set;

/**
 * Status do fluxo de agendamento. O nome usado no banco segue o modelo do grupo:
 * a consulta confirmada fica AGENDADA e a concluída fica REALIZADA.
 */
public enum StatusConsulta {
    SOLICITADA("SOLICITADA", "Aguardando confirmação"),
    CONFIRMADA("AGENDADA", "Confirmada"),
    RECUSADA("RECUSADA", "Recusada"),
    CANCELADA("CANCELADA", "Cancelada"),
    CONCLUIDA("REALIZADA", "Concluída");

    private static final Set<StatusConsulta> OCUPAM_AGENDA = Set.of(SOLICITADA, CONFIRMADA);

    private final String valorNoBanco;
    private final String descricao;

    StatusConsulta(String valorNoBanco, String descricao) {
        this.valorNoBanco = valorNoBanco;
        this.descricao = descricao;
    }

    public static StatusConsulta doValorNoBanco(String valor) {
        return Arrays.stream(values())
                .filter(status -> status.valorNoBanco.equals(valor))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Status de consulta desconhecido: " + valor));
    }

    public static Set<StatusConsulta> queOcupamAgenda() {
        return OCUPAM_AGENDA;
    }

    public boolean ocupaAgenda() {
        return OCUPAM_AGENDA.contains(this);
    }

    public String getValorNoBanco() {
        return valorNoBanco;
    }

    public String getDescricao() {
        return descricao;
    }
}
