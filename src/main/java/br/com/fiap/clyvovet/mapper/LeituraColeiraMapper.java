package br.com.fiap.clyvovet.mapper;

import br.com.fiap.clyvovet.dto.response.LeituraColeiraResponse;
import br.com.fiap.clyvovet.model.LeituraColeira;
import org.springframework.stereotype.Component;

@Component
public class LeituraColeiraMapper {

    public LeituraColeiraResponse paraResponse(LeituraColeira leitura) {
        return new LeituraColeiraResponse(
                leitura.getId(),
                leitura.getDataHora(),
                leitura.getBatimentoBpm(),
                leitura.isAlerta(),
                leitura.getLatitude(),
                leitura.getLongitude(),
                leitura.getPet().getNome()
        );
    }
}
