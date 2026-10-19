package br.com.fiap.clyvovet.mapper;

import br.com.fiap.clyvovet.controller.AnexoController;
import br.com.fiap.clyvovet.dto.response.AnexoResponse;
import br.com.fiap.clyvovet.model.Anexo;
import org.springframework.hateoas.Link;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class AnexoMapper {
    public AnexoResponse anexoToResponse(Anexo anexo) {

        Link link = linkTo(
                methodOn(AnexoController.class)
                        .readAnexo(anexo.getAnexoId())
        ).withRel("Detalhes do anexo");

        return new AnexoResponse(
                anexo.getAnexoId(),
                anexo.getNomeArquivo(),
                anexo.getTipoArquivo(),
                anexo.getUrl(),
                anexo.getTamanhoKb(),
                anexo.getDataCriacao(),
                anexo.getUsuario().getUsuarioId(),
                link
        );
    }
}
