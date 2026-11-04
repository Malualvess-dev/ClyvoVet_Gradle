package br.com.fiap.clyvovet.dto.api;

/** Regras de formato iguais às das telas web, para o app e o site aceitarem as mesmas contas. */
final class FormatosDoAplicativo {

    static final String FORMATO = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).*$";
    static final String MENSAGEM = "A senha deve conter letra maiúscula, letra minúscula e número.";
    static final String TELEFONE = "^\\(?\\d{2}\\)?\\s?9?\\d{4}-?\\d{4}$";
    static final String MENSAGEM_TELEFONE = "Informe um telefone válido, ex.: (11) 98765-4321.";

    private FormatosDoAplicativo() {
    }
}
