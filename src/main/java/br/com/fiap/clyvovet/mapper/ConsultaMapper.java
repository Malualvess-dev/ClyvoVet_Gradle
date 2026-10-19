package br.com.fiap.clyvovet.mapper;

import br.com.fiap.clyvovet.dto.response.ConsultaResponse;
import br.com.fiap.clyvovet.model.Consulta;
import br.com.fiap.clyvovet.model.Tutor;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.LocalDateTime;

@Component
public class ConsultaMapper {

    private final PetMapper petMapper;
    private final Clock relogio;

    public ConsultaMapper(PetMapper petMapper, Clock relogio) {
        this.petMapper = petMapper;
        this.relogio = relogio;
    }

    public ConsultaResponse paraResponse(Consulta consulta) {
        Tutor tutor = consulta.getPet().getTutor();
        return new ConsultaResponse(
                consulta.getId(),
                consulta.getDataHora(),
                consulta.getMotivo(),
                consulta.getStatus(),
                consulta.getJustificativa(),
                consulta.getDiagnostico(),
                consulta.getPrescricao(),
                consulta.getPesoAferidoKg(),
                petMapper.paraResponse(consulta.getPet()),
                tutor.getNome(),
                tutor.getTelefone(),
                consulta.getVeterinario().getNome(),
                consulta.podeSerCancelada(LocalDateTime.now(relogio))
        );
    }
}
