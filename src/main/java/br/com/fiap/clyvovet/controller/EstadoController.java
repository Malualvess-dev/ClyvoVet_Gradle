package br.com.fiap.clyvovet.controller;

import br.com.fiap.clyvovet.dto.request.EstadoRequest;
import br.com.fiap.clyvovet.dto.response.ApiResponse;
import br.com.fiap.clyvovet.dto.response.EstadoResponse;
import br.com.fiap.clyvovet.model.Estado;
import br.com.fiap.clyvovet.service.EstadoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/estados")
@Tag(name = "api-estados")
public class EstadoController {

    private final EstadoService estadoService;

    public EstadoController(EstadoService estadoService) {
        this.estadoService = estadoService;
    }

    //CREATE

    @Operation(summary = "Cria um novo estado")
    @PostMapping
    public ResponseEntity<ApiResponse> createEstado(
            @Valid @RequestBody EstadoRequest estadoRequest
    ) {

        estadoService.create(estadoRequest);

        return new ResponseEntity<>(
                new ApiResponse("Estado cadastrado com sucesso"),
                HttpStatus.CREATED
        );
    }

    //Acha os estados por ID
    @Operation(summary = "Busca um estado por id")
    @GetMapping("/{id}")
    public ResponseEntity<EstadoResponse> readEstado(
            @PathVariable Integer id
    ) {

        EstadoResponse estado = estadoService.readEstado(id);

        return new ResponseEntity<>(
                estado,
                HttpStatus.OK
        );
    }

    //Lista todos os estados
    @Operation(summary = "Lista todos os estados")
    @GetMapping
    public ResponseEntity<Page<EstadoResponse>> readEstados(
            Pageable pageable
    ) {

        return new ResponseEntity<>(
                estadoService.read(pageable),
                HttpStatus.OK
        );
    }

    //Busca estados pela UF
    @Operation(summary = "Busca estado pela UF")
    @GetMapping("/uf/{uf}")
    public ResponseEntity<EstadoResponse> readByUf(
            @PathVariable String uf
    ) {

        EstadoResponse estado = estadoService.readByUf(uf);

        return new ResponseEntity<>(
                estado,
                HttpStatus.OK
        );
    }

    //Atualiza os estados
    @Operation(summary = "Atualiza um estado")
    @PutMapping("/{id}")
    public ResponseEntity<EstadoResponse> updateEstado(
            @PathVariable Integer id,
            @Valid @RequestBody EstadoRequest estadoRequest
    ) {

        EstadoResponse estadoAtualizado =
                estadoService.update(id, estadoRequest);

        return new ResponseEntity<>(
                estadoAtualizado,
                HttpStatus.OK
        );
    }

    //Deleta um estado

    @Operation(summary = "Remove um estado")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteEstado(
            @PathVariable Integer id
    ) {

        estadoService.delete(id);

        return new ResponseEntity<>(
                "Estado removido com sucesso",
                HttpStatus.OK
        );
    }
}
