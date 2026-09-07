package br.com.fiap.clyvovet.mapper;

import br.com.fiap.clyvovet.controller.VeterinarioController;
import br.com.fiap.clyvovet.dto.response.VeterinarioResponse;
import br.com.fiap.clyvovet.model.Veterinario;
import org.springframework.hateoas.Link;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class VeterinarioMapper {

    public VeterinarioResponse veterinarioToResponse(
            Veterinario veterinario
    ) {

        Link link = linkTo(
                methodOn(VeterinarioController.class)
                        .readVeterinario(
                                veterinario.getVeterinarioId()
                        )
        ).withRel("Detalhes do veterinário");

        return new VeterinarioResponse(
                veterinario.getVeterinarioId(),
                veterinario.getNomeVeterinario(),
                veterinario.getCrmv(),
                veterinario.getTelefone(),
                veterinario.getEspecialidade(),
                veterinario.getUsuario().getUsuarioId(),
                link
        );
    }
}