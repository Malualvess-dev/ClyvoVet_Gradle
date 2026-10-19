package br.com.fiap.clyvovet.model;

import java.util.Set;

public enum StatusConsulta {
    SOLICITADA("Aguardando confirmação"),
    CONFIRMADA("Confirmada"),
    RECUSADA("Recusada"),
    CANCELADA("Cancelada"),
    CONCLUIDA("Concluída");

    private static final Set<StatusConsulta> OCUPAM_AGENDA = Set.of(SOLICITADA, CONFIRMADA);

    private final String descricao;

    StatusConsulta(String descricao) {
        this.descricao = descricao;
    }

    public static Set<StatusConsulta> queOcupamAgenda() {
        return OCUPAM_AGENDA;
    }

    public boolean ocupaAgenda() {
        return OCUPAM_AGENDA.contains(this);
    }

    public String getDescricao() {
        return descricao;
    }
}
