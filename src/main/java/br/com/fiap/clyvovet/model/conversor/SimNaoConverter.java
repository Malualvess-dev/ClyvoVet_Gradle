package br.com.fiap.clyvovet.model.conversor;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/** Colunas CHAR(1) do modelo do grupo que guardam 'S' (sim) ou 'N' (não). */
@Converter
public class SimNaoConverter implements AttributeConverter<Boolean, String> {

    private static final String SIM = "S";
    private static final String NAO = "N";

    @Override
    public String convertToDatabaseColumn(Boolean valor) {
        return Boolean.TRUE.equals(valor) ? SIM : NAO;
    }

    @Override
    public Boolean convertToEntityAttribute(String valor) {
        return valor != null && SIM.equals(valor.strip());
    }
}
