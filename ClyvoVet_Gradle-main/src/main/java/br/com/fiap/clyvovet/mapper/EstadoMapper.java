package br.com.fiap.clyvovet.mapper;

import br.com.fiap.clyvovet.controller.EstadoController;
import br.com.fiap.clyvovet.dto.response.EstadoResponse;
import br.com.fiap.clyvovet.model.Estado;
import org.springframework.hateoas.Link;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class EstadoMapper {

    public EstadoResponse estadoToResponse(Estado estado) {

        Link link = linkTo(
                methodOn(EstadoController.class)
                        .readEstado(estado.getEstadoId())
        ).withRel("Detalhes do estado");

        return new EstadoResponse(
                estado.getEstadoId(),
                estado.getNome(),
                estado.getUf(),
                link
        );
    }
}
