package br.com.fiap.clyvovet.model.conversor;

import br.com.fiap.clyvovet.model.StatusConsulta;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class StatusConsultaConverter implements AttributeConverter<StatusConsulta, String> {

    @Override
    public String convertToDatabaseColumn(StatusConsulta status) {
        return status == null ? null : status.getValorNoBanco();
    }

    @Override
    public StatusConsulta convertToEntityAttribute(String valor) {
        return valor == null ? null : StatusConsulta.doValorNoBanco(valor.strip());
    }
}
