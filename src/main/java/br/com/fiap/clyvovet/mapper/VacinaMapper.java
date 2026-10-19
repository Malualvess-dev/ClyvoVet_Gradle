package br.com.fiap.clyvovet.mapper;

import br.com.fiap.clyvovet.dto.response.VacinaResponse;
import br.com.fiap.clyvovet.model.Vacina;
import org.springframework.stereotype.Component;

@Component
public class VacinaMapper {

    public VacinaResponse paraResponse(Vacina vacina) {
        return new VacinaResponse(
                vacina.getId(),
                vacina.getNome(),
                vacina.getDataAplicacao(),
                vacina.getProximaDose(),
                vacina.getPet().getNome()
        );
    }
}
