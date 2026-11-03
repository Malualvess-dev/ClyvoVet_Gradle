package br.com.fiap.clyvovet.model;

public enum Modalidade {
    PRESENCIAL("Presencial"),
    ONLINE("Online");

    private final String descricao;

    Modalidade(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
