package br.com.fiap.clyvovet.mapper;

import br.com.fiap.clyvovet.controller.CidadeController;
import br.com.fiap.clyvovet.dto.response.CidadeResponse;
import br.com.fiap.clyvovet.model.Cidade;
import org.springframework.hateoas.Link;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class CidadeMapper {
    public CidadeResponse cidadeToResponse(Cidade cidade) {

        Link link = linkTo(
                methodOn(CidadeController.class)
                        .readCidade(cidade.getCidadeId())
        ).withRel("Detalhes da cidade");

        return new CidadeResponse(
                cidade.getCidadeId(),
                cidade.getNome(),
                cidade.getEstado().getEstadoId(),
                link
        );
    }
}
