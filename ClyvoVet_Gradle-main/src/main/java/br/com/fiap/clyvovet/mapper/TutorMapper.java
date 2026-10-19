package br.com.fiap.clyvovet.mapper;

import br.com.fiap.clyvovet.controller.TutorController;
import br.com.fiap.clyvovet.dto.response.TutorResponse;
import br.com.fiap.clyvovet.model.Tutor;
import org.springframework.hateoas.Link;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class TutorMapper {
    public TutorResponse tutorToResponse(Tutor tutor) {

        Link link = linkTo(
                methodOn(TutorController.class)
                        .readTutor(tutor.getTutorId())
        ).withRel("Detalhes do tutor");

        return new TutorResponse(
                tutor.getTutorId(),
                tutor.getNomeTutor(),
                tutor.getCpf(),
                tutor.getTelefone(),
                tutor.getDataNascimento(),
                tutor.getUsuario().getUsuarioId(),
                link
        );
    }
}
