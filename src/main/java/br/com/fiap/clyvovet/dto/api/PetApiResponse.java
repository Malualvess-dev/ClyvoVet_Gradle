package br.com.fiap.clyvovet.dto.api;

import java.math.BigDecimal;

/** Pet no formato usado pelas telas do aplicativo (ids como texto, datas e peso já formatados). */
public record PetApiResponse(
        String id,
        String tutorId,
        String tutorNome,
        String nome,
        String especie,
        String racaId,
        String raca,
        String porte,
        String sexo,
        String sexoCodigo,
        String dataNascimento,
        String idade,
        BigDecimal pesoKg,
        String peso,
        String observacoes
) {
}
