package br.com.fiap.clyvovet.dto.response;

import org.springframework.hateoas.Link;

public record VeterinarioResponse(

        Integer veterinarioId,
        String nomeVeterinario,
        String crmv,
        String telefone,
        String especialidade,
        Integer usuarioId,
        Link link

) {
}