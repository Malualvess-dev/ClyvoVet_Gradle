package br.com.fiap.clyvovet.mapper;

import br.com.fiap.clyvovet.dto.response.ConsultaResponse;
import br.com.fiap.clyvovet.dto.response.PrescricaoResponse;
import br.com.fiap.clyvovet.dto.response.ProntuarioResponse;
import br.com.fiap.clyvovet.model.Consulta;
import br.com.fiap.clyvovet.model.Prescricao;
import br.com.fiap.clyvovet.model.Prontuario;
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
                consulta.getModalidade(),
                consulta.getMotivo(),
                consulta.getStatus(),
                consulta.getJustificativa(),
                consulta.getProntuario() == null ? null : prontuarioResponse(consulta.getProntuario()),
                petMapper.paraResponse(consulta.getPet()),
                tutor.getNome(),
                tutor.getTelefone(),
                consulta.getVeterinario().getNome(),
                consulta.podeSerCancelada(LocalDateTime.now(relogio))
        );
    }

    private static ProntuarioResponse prontuarioResponse(Prontuario prontuario) {
        return new ProntuarioResponse(
                prontuario.getDiagnostico(),
                prontuario.getSintomas(),
                prontuario.getTratamento(),
                prontuario.getPesoAnimal(),
                prontuario.getTemperatura(),
                prontuario.getExamesSolicitados(),
                prontuario.getDataRetorno(),
                prontuario.getPrescricoes().stream().map(ConsultaMapper::prescricaoResponse).toList()
        );
    }

    private static PrescricaoResponse prescricaoResponse(Prescricao prescricao) {
        return new PrescricaoResponse(prescricao.getTipo(), prescricao.getDescricao(), prescricao.getPosologia());
    }
}
