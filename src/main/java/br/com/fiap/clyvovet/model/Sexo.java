package br.com.fiap.clyvovet.model;

public enum Sexo {
    M("Macho"),
    F("Fêmea");

    private final String descricao;

    Sexo(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
