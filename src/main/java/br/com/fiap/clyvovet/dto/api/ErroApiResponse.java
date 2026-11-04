package br.com.fiap.clyvovet.dto.api;

import java.util.Map;

public record ErroApiResponse(int status, String mensagem, Map<String, String> campos) {

    public static ErroApiResponse de(int status, String mensagem) {
        return new ErroApiResponse(status, mensagem, Map.of());
    }
}
