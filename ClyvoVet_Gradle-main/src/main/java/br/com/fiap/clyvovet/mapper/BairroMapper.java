package br.com.fiap.clyvovet.mapper;

import br.com.fiap.clyvovet.controller.BairroController;
import br.com.fiap.clyvovet.dto.response.BairroResponse;
import br.com.fiap.clyvovet.model.Bairro;
import org.springframework.hateoas.Link;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class BairroMapper {
    public BairroResponse bairroToResponse(Bairro bairro) {

        Link link = linkTo(
                methodOn(BairroController.class)
                        .readBairro(bairro.getBairroId())
        ).withRel("Detalhes do bairro");

        return new BairroResponse(
                bairro.getBairroId(),
                bairro.getNome(),
                bairro.getCidade().getCidadeId(),
                link
        );
    }
}
