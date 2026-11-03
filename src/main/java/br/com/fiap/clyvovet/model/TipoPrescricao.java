package br.com.fiap.clyvovet.model;

public enum TipoPrescricao {
    MEDICAMENTO("Medicamento"),
    DIETA("Dieta"),
    RESTRICAO("Restrição");

    private final String descricao;

    TipoPrescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
