package br.com.fiap.clyvovet.dto.response;

/** Resultado da chamada de uma procedure do banco: o conteúdo devolvido ou a mensagem de erro. */
public record ResultadoProcedimento(
        String procedimento,
        boolean sucesso,
        String conteudo,
        String erro
) {

    public static ResultadoProcedimento sucesso(String procedimento, String conteudo) {
        return new ResultadoProcedimento(procedimento, true, conteudo, null);
    }

    public static ResultadoProcedimento falha(String procedimento, String erro) {
        return new ResultadoProcedimento(procedimento, false, null, erro);
    }
}
