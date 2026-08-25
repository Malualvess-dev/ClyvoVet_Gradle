package br.com.fiap.clyvovet.controller;

import br.com.fiap.clyvovet.dto.request.CidadeRequest;
import br.com.fiap.clyvovet.dto.response.ApiResponse;
import br.com.fiap.clyvovet.dto.response.CidadeResponse;
import br.com.fiap.clyvovet.service.CidadeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cidades")
@Tag(name = "api-cidades")
public class CidadeController {

    private final CidadeService cidadeService;

    public CidadeController(CidadeService cidadeService) {
        this.cidadeService = cidadeService;
    }

    @Operation(summary = "Cria uma nova cidade")
    @PostMapping
    public ResponseEntity<ApiResponse> createCidade(
            @Valid @RequestBody CidadeRequest cidadeRequest
    ) {

        cidadeService.create(cidadeRequest);

        return new ResponseEntity<>(
                new ApiResponse("Cidade cadastrada com sucesso"),
                HttpStatus.CREATED
        );
    }

    @Operation(summary = "Busca cidade por id")
    @GetMapping("/{id}")
    public ResponseEntity<CidadeResponse> readCidade(
            @PathVariable Integer id
    ) {

        CidadeResponse cidade =
                cidadeService.readCidade(id);

        return new ResponseEntity<>(
                cidade,
                HttpStatus.OK
        );
    }

    @Operation(summary = "Lista cidades")
    @GetMapping
    public ResponseEntity<Page<CidadeResponse>> readCidades(
            Pageable pageable
    ) {

        return new ResponseEntity<>(
                cidadeService.read(pageable),
                HttpStatus.OK
        );
    }

    @Operation(summary = "Busca cidade pelo nome")
    @GetMapping("/nome")
    public ResponseEntity<Page<CidadeResponse>> readByNome(
            @RequestParam String nome,
            Pageable pageable
    ) {

        return new ResponseEntity<>(
                cidadeService.readByNome(nome, pageable),
                HttpStatus.OK
        );
    }

    @Operation(summary = "Atualiza cidade")
    @PutMapping("/{id}")
    public ResponseEntity<CidadeResponse> updateCidade(
            @PathVariable Integer id,
            @Valid @RequestBody CidadeRequest cidadeRequest
    ) {

        CidadeResponse cidadeAtualizada =
                cidadeService.update(id, cidadeRequest);

        return new ResponseEntity<>(
                cidadeAtualizada,
                HttpStatus.OK
        );
    }

    @Operation(summary = "Remove cidade")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCidade(
            @PathVariable Integer id
    ) {

        cidadeService.delete(id);

        return new ResponseEntity<>(
                "Cidade removida com sucesso",
                HttpStatus.OK
        );
    }
}
