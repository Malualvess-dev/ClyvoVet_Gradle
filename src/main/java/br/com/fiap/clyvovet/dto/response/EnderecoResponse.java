package br.com.fiap.clyvovet.dto.response;

public record EnderecoResponse(
        String logradouro,
        String numero,
        String complemento,
        String cep,
        Long bairroId,
        String bairro
) {
}
