package br.com.fiap.clyvovet.controller;

import br.com.fiap.clyvovet.dto.request.VeterinarioRequest;
import br.com.fiap.clyvovet.dto.response.ApiResponse;
import br.com.fiap.clyvovet.dto.response.VeterinarioResponse;
import br.com.fiap.clyvovet.service.VeterinarioService;
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
@RequestMapping("/veterinarios")
@Tag(name = "api-veterinarios")
public class VeterinarioController {

    private final VeterinarioService veterinarioService;

    public VeterinarioController(
            VeterinarioService veterinarioService
    ) {
        this.veterinarioService = veterinarioService;
    }

    // CREATE
    @Operation(summary = "Cadastra um novo veterinário")
    @PostMapping
    public ResponseEntity<ApiResponse> createVeterinario(
            @Valid @RequestBody VeterinarioRequest request
    ) {

        veterinarioService.create(request);

        return new ResponseEntity<>(
                new ApiResponse(
                        "Veterinário cadastrado com sucesso"
                ),
                HttpStatus.CREATED
        );
    }

    // READ POR ID
    @Operation(summary = "Busca veterinário por id")
    @GetMapping("/{id}")
    public ResponseEntity<VeterinarioResponse> readVeterinario(
            @PathVariable Integer id
    ) {

        return new ResponseEntity<>(
                veterinarioService.readVeterinario(id),
                HttpStatus.OK
        );
    }

    // READ TODOS
    @Operation(summary = "Lista todos os veterinários")
    @GetMapping
    public ResponseEntity<Page<VeterinarioResponse>> readVeterinarios(
            @ParameterObject Pageable pageable
    ) {

        return new ResponseEntity<>(
                veterinarioService.read(pageable),
                HttpStatus.OK
        );
    }

    // READ POR CRMV
    @Operation(summary = "Busca veterinário pelo CRMV")
    @GetMapping("/crmv")
    public ResponseEntity<VeterinarioResponse> readByCrmv(
            @RequestParam String crmv
    ) {

        return new ResponseEntity<>(
                veterinarioService.readByCrmv(crmv),
                HttpStatus.OK
        );
    }

    // UPDATE
    @Operation(summary = "Atualiza um veterinário")
    @PutMapping("/{id}")
    public ResponseEntity<VeterinarioResponse> updateVeterinario(
            @PathVariable Integer id,
            @Valid @RequestBody VeterinarioRequest request
    ) {

        return new ResponseEntity<>(
                veterinarioService.update(id, request),
                HttpStatus.OK
        );
    }

    // DELETE
    @Operation(summary = "Remove um veterinário")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteVeterinario(
            @PathVariable Integer id
    ) {

        veterinarioService.delete(id);

        return new ResponseEntity<>(
                "Veterinário removido com sucesso",
                HttpStatus.OK
        );
    }
}