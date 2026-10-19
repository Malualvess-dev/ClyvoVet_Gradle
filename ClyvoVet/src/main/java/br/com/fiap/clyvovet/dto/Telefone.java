package br.com.fiap.clyvovet.dto;

final class Telefone {

    static final String FORMATO = "^\\(?\\d{2}\\)?\\s?9?\\d{4}-?\\d{4}$";
    static final String MENSAGEM = "Informe um telefone válido, ex.: (11) 98765-4321.";

    private Telefone() {
    }
}
