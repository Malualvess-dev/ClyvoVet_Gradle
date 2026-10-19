package br.com.fiap.clyvovet.controller;

import br.com.fiap.clyvovet.dto.request.BairroRequest;
import br.com.fiap.clyvovet.dto.response.ApiResponse;
import br.com.fiap.clyvovet.dto.response.BairroResponse;
import br.com.fiap.clyvovet.service.BairroService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/bairros")
@Tag(name = "api-bairros")
public class BairroController {

    private final BairroService bairroService;

    public BairroController(BairroService bairroService) {
        this.bairroService = bairroService;
    }

    // CREATE
    @Operation(summary = "Cria um novo bairro")
    @PostMapping
    public ResponseEntity<ApiResponse> createBairro(
            @Valid @RequestBody BairroRequest bairroRequest
    ) {

        bairroService.create(bairroRequest);

        return new ResponseEntity<>(
                new ApiResponse("Bairro cadastrado com sucesso"),
                HttpStatus.CREATED
        );
    }

    // READ POR ID
    @Operation(summary = "Busca bairro por id")
    @GetMapping("/{id}")
    public ResponseEntity<BairroResponse> readBairro(
            @PathVariable Integer id
    ) {

        return new ResponseEntity<>(
                bairroService.readBairro(id),
                HttpStatus.OK
        );
    }

    // READ TODOS
    @Operation(summary = "Lista bairros")
    @GetMapping
    public ResponseEntity<Page<BairroResponse>> readBairros(
            @ParameterObject Pageable pageable
    ) {

        return new ResponseEntity<>(
                bairroService.read(pageable),
                HttpStatus.OK
        );
    }

    // READ POR NOME
    @Operation(summary = "Busca bairro por nome")
    @GetMapping("/nome")
    public ResponseEntity<Page<BairroResponse>> readByNome(
            @RequestParam String nome,
            @ParameterObject Pageable pageable
    ) {

        return new ResponseEntity<>(
                bairroService.readByNome(nome, pageable),
                HttpStatus.OK
        );
    }

    // UPDATE
    @Operation(summary = "Atualiza bairro")
    @PutMapping("/{id}")
    public ResponseEntity<BairroResponse> updateBairro(
            @PathVariable Integer id,
            @Valid @RequestBody BairroRequest bairroRequest
    ) {

        return new ResponseEntity<>(
                bairroService.update(id, bairroRequest),
                HttpStatus.OK
        );
    }

    // DELETE
    @Operation(summary = "Remove bairro")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteBairro(
            @PathVariable Integer id
    ) {

        bairroService.delete(id);

        return new ResponseEntity<>(
                "Bairro removido com sucesso",
                HttpStatus.OK
        );
    }
}