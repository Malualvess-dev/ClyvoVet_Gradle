package br.com.fiap.clyvovet.dto.response;

import java.time.LocalDate;

public record TutorResponse(
        Integer tutorId,
        String nomeTutor,
        String cpf,
        String telefone,
        LocalDate dataNascimento,
        Integer usuarioId
) {
}
