package br.com.fiap.clyvovet.mapper;

import br.com.fiap.clyvovet.controller.UsuarioController;
import br.com.fiap.clyvovet.dto.response.UsuarioResponse;
import br.com.fiap.clyvovet.model.Usuario;
import org.springframework.hateoas.Link;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class UsuarioMapper {
    public UsuarioResponse usuarioToResponse(Usuario usuario) {

        Link link = linkTo(
                methodOn(UsuarioController.class)
                        .readUsuario(usuario.getUsuarioId())
        ).withRel("Detalhes do usuário");

        return new UsuarioResponse(
                usuario.getUsuarioId(),
                usuario.getEmail(),
                usuario.getTipoUser(),
                usuario.getDataCriacao(),
                link
        );
    }
}
