package br.com.fiap.clyvovet.mapper;

import br.com.fiap.clyvovet.controller.AgendaController;
import br.com.fiap.clyvovet.dto.response.AgendaResponse;
import br.com.fiap.clyvovet.model.Agenda;
import org.springframework.hateoas.Link;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class AgendaMapper {
    public AgendaResponse agendaToResponse(Agenda agenda) {

        Link link = linkTo(
                methodOn(AgendaController.class)
                        .readAgenda(agenda.getAgendaId())
        ).withRel("Detalhes da agenda");

        return new AgendaResponse(
                agenda.getAgendaId(),
                agenda.getTitulo(),
                agenda.getDataHora(),
                agenda.getAnotacao(),
                agenda.getUsuario().getUsuarioId(),
                link
        );
    }
}
