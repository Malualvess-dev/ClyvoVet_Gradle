package br.com.fiap.clyvovet.controller;


import br.com.fiap.clyvovet.dto.request.AnexoRequest;
import br.com.fiap.clyvovet.dto.response.AnexoResponse;
import br.com.fiap.clyvovet.dto.response.ApiResponse;
import br.com.fiap.clyvovet.model.TipoArquivo;
import br.com.fiap.clyvovet.service.AnexoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/anexos")
@Tag(name = "api-anexos")
public class AnexoController {

    private final AnexoService anexoService;

    public AnexoController(AnexoService anexoService) {
        this.anexoService = anexoService;
    }

    @Operation(summary = "Cria um novo anexo")
    @PostMapping
    public ResponseEntity<ApiResponse> createAnexo(
            @Valid @RequestBody AnexoRequest anexoRequest
    ) {

        anexoService.create(anexoRequest);

        return new ResponseEntity<>(
                new ApiResponse("Anexo cadastrado com sucesso"),
                HttpStatus.CREATED
        );
    }

    @Operation(summary = "Busca anexo por id")
    @GetMapping("/{id}")
    public ResponseEntity<AnexoResponse> readAnexo(@PathVariable Integer id) {
        return new ResponseEntity<>(anexoService.readAnexo(id), HttpStatus.OK);
    }

    @Operation(summary = "Lista anexos")
    @GetMapping
    public ResponseEntity<Page<AnexoResponse>> readAnexos(Pageable pageable) {
        return new ResponseEntity<>(anexoService.read(pageable), HttpStatus.OK);
    }

    @Operation(summary = "Busca anexos por tipo de arquivo")
    @GetMapping("/tipo")
    public ResponseEntity<Page<AnexoResponse>> readByTipoArquivo(@RequestParam TipoArquivo tipoArquivo, Pageable pageable) {
        return new ResponseEntity<>(anexoService.readByTipoArquivo(tipoArquivo, pageable), HttpStatus.OK);
    }

    @Operation(summary = "Atualiza anexo")
    @PutMapping("/{id}")
    public ResponseEntity<AnexoResponse> updateAnexo(@PathVariable Integer id, @Valid @RequestBody AnexoRequest anexoRequest) {
        return new ResponseEntity<>(anexoService.update(id, anexoRequest), HttpStatus.OK);
    }

    @Operation(summary = "Remove anexo")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteAnexo(
            @PathVariable Integer id
    ) {

        anexoService.delete(id);

        return new ResponseEntity<>(
                "Anexo removido com sucesso",
                HttpStatus.OK
        );
    }
}
