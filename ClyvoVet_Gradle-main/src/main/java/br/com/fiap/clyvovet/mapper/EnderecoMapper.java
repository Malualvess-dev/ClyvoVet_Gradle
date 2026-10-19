package br.com.fiap.clyvovet.mapper;

import br.com.fiap.clyvovet.controller.EnderecoController;
import br.com.fiap.clyvovet.dto.response.EnderecoResponse;
import br.com.fiap.clyvovet.model.Endereco;
import org.springframework.hateoas.Link;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class EnderecoMapper {

    public EnderecoResponse enderecoToResponse(Endereco endereco) {

        Link link = linkTo(
                methodOn(EnderecoController.class)
                        .readEndereco(endereco.getEnderecoId())
        ).withRel("Detalhes do endereço");

        return new EnderecoResponse(
                endereco.getEnderecoId(),
                endereco.getLogradouro(),
                endereco.getNumero(),
                endereco.getComplemento(),
                endereco.getCep(),
                endereco.getLatitude(),
                endereco.getLongitude(),
                endereco.getUsuario().getUsuarioId(),
                endereco.getBairro().getBairroId(),
                link
        );
    }
}
