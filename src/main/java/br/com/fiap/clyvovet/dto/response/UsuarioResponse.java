package br.com.fiap.clyvovet.dto.response;

import br.com.fiap.clyvovet.model.TipoUsuario;

import java.time.LocalDateTime;

public record UsuarioResponse(
        Integer usuarioId,
        String email,
        TipoUsuario tipoUser,
        LocalDateTime dataCriacao
) {
}
