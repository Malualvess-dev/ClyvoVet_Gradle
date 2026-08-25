package br.com.fiap.clyvovet.controller;


import br.com.fiap.clyvovet.dto.request.UsuarioRequest;
import br.com.fiap.clyvovet.dto.response.ApiResponse;
import br.com.fiap.clyvovet.dto.response.UsuarioResponse;
import br.com.fiap.clyvovet.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/usuarios")
@Tag(name = "api-usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @Operation(summary = "Cria um novo usuário")
    @PostMapping
    public ResponseEntity<ApiResponse> createUsuario(
            @Valid @RequestBody UsuarioRequest usuarioRequest
    ) {

        usuarioService.create(usuarioRequest);

        return new ResponseEntity<>(
                new ApiResponse("Usuário cadastrado com sucesso"),
                HttpStatus.CREATED
        );
    }

    @Operation(summary = "Busca usuário por id")
    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponse> readUsuario(@PathVariable Integer id) {
        return new ResponseEntity<>(usuarioService.readUsuario(id), HttpStatus.OK);
    }

    @Operation(summary = "Lista usuários")
    @GetMapping
    public ResponseEntity<Page<UsuarioResponse>> readUsuarios(Pageable pageable) {
        return new ResponseEntity<>(usuarioService.read(pageable), HttpStatus.OK);
    }

    @Operation(summary = "Busca usuário por email")
    @GetMapping("/email")
    public ResponseEntity<UsuarioResponse> readByEmail(@RequestParam String email) {
        return new ResponseEntity<>(usuarioService.readByEmail(email), HttpStatus.OK);
    }

    @Operation(summary = "Atualiza usuário")
    @PutMapping("/{id}")
    public ResponseEntity<UsuarioResponse> updateUsuario(@PathVariable Integer id, @Valid @RequestBody UsuarioRequest usuarioRequest) {
        return new ResponseEntity<>(usuarioService.update(id, usuarioRequest), HttpStatus.OK);
    }

    @Operation(summary = "Remove usuário")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUsuario(
            @PathVariable Integer id
    ) {

        usuarioService.delete(id);

        return new ResponseEntity<>(
                "Usuário removido com sucesso",
                HttpStatus.OK
        );
    }
}
