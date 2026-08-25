package br.com.fiap.clyvovet.controller;


import br.com.fiap.clyvovet.dto.request.EnderecoRequest;
import br.com.fiap.clyvovet.dto.response.ApiResponse;
import br.com.fiap.clyvovet.dto.response.EnderecoResponse;
import br.com.fiap.clyvovet.service.EnderecoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/enderecos")
@Tag(name = "api-enderecos")
public class EnderecoController {

    private final EnderecoService enderecoService;

    public EnderecoController(EnderecoService enderecoService) {
        this.enderecoService = enderecoService;
    }

    @Operation(summary = "Cria um novo endereço")
    @PostMapping
    public ResponseEntity<ApiResponse> createEndereco(
            @Valid @RequestBody EnderecoRequest enderecoRequest
    ) {

        enderecoService.create(enderecoRequest);

        return new ResponseEntity<>(
                new ApiResponse("Endereço cadastrado com sucesso"),
                HttpStatus.CREATED
        );
    }

    @Operation(summary = "Busca endereço por id")
    @GetMapping("/{id}")
    public ResponseEntity<EnderecoResponse> readEndereco(@PathVariable Integer id) {
        return new ResponseEntity<>(enderecoService.readEndereco(id), HttpStatus.OK);
    }

    @Operation(summary = "Lista endereços")
    @GetMapping
    public ResponseEntity<Page<EnderecoResponse>> readEnderecos(Pageable pageable) {
        return new ResponseEntity<>(enderecoService.read(pageable), HttpStatus.OK);
    }

    @Operation(summary = "Busca endereço por CEP")
    @GetMapping("/cep")
    public ResponseEntity<Page<EnderecoResponse>> readByCep(@RequestParam String cep, Pageable pageable) {
        return new ResponseEntity<>(enderecoService.readByCep(cep, pageable), HttpStatus.OK);
    }

    @Operation(summary = "Atualiza endereço")
    @PutMapping("/{id}")
    public ResponseEntity<EnderecoResponse> updateEndereco(@PathVariable Integer id, @Valid @RequestBody EnderecoRequest enderecoRequest) {
        return new ResponseEntity<>(enderecoService.update(id, enderecoRequest), HttpStatus.OK);
    }

    @Operation(summary = "Remove endereço")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteEndereco(
            @PathVariable Integer id
    ) {

        enderecoService.delete(id);

        return new ResponseEntity<>(
                "Endereço removido com sucesso",
                HttpStatus.OK
        );
    }
}
