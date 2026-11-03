package br.com.fiap.clyvovet.mapper;

import br.com.fiap.clyvovet.dto.response.HistoricoSaudeResponse;
import br.com.fiap.clyvovet.model.HistoricoSaude;
import org.springframework.stereotype.Component;

@Component
public class HistoricoSaudeMapper {

    public HistoricoSaudeResponse paraResponse(HistoricoSaude evento) {
        return new HistoricoSaudeResponse(
                evento.getId(),
                evento.getEvento(),
                evento.getDataEvento(),
                evento.getDetalhes(),
                evento.getProximaDose(),
                evento.getPet().getNome()
        );
    }
}
