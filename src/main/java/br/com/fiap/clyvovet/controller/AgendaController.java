package br.com.fiap.clyvovet.controller;


import br.com.fiap.clyvovet.dto.request.AgendaRequest;
import br.com.fiap.clyvovet.dto.response.AgendaResponse;
import br.com.fiap.clyvovet.dto.response.ApiResponse;
import br.com.fiap.clyvovet.service.AgendaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/agendas")
@Tag(name = "api-agendas")
public class AgendaController {

    private final AgendaService agendaService;

    public AgendaController(AgendaService agendaService) {
        this.agendaService = agendaService;
    }

    @Operation(summary = "Cria uma nova agenda")
    @PostMapping
    public ResponseEntity<ApiResponse> createAgenda(
            @Valid @RequestBody AgendaRequest agendaRequest
    ) {

        agendaService.create(agendaRequest);

        return new ResponseEntity<>(
                new ApiResponse("Agenda cadastrada com sucesso"),
                HttpStatus.CREATED
        );
    }

    @Operation(summary = "Busca agenda por id")
    @GetMapping("/{id}")
    public ResponseEntity<AgendaResponse> readAgenda(@PathVariable Integer id) {
        return new ResponseEntity<>(agendaService.readAgenda(id), HttpStatus.OK);
    }

    @Operation(summary = "Lista agendas")
    @GetMapping
    public ResponseEntity<Page<AgendaResponse>> readAgendas(Pageable pageable) {
        return new ResponseEntity<>(agendaService.read(pageable), HttpStatus.OK);
    }

    @Operation(summary = "Busca agenda por período")
    @GetMapping("/periodo")
    public ResponseEntity<Page<AgendaResponse>> readByPeriodo(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fim,
            Pageable pageable
    ) {
        return new ResponseEntity<>(agendaService.readByPeriodo(inicio, fim, pageable), HttpStatus.OK);
    }

    @Operation(summary = "Atualiza agenda")
    @PutMapping("/{id}")
    public ResponseEntity<AgendaResponse> updateAgenda(@PathVariable Integer id, @Valid @RequestBody AgendaRequest agendaRequest) {
        return new ResponseEntity<>(agendaService.update(id, agendaRequest), HttpStatus.OK);
    }

    @Operation(summary = "Remove agenda")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteAgenda(
            @PathVariable Integer id
    ) {

        agendaService.delete(id);

        return new ResponseEntity<>(
                "Agenda removida com sucesso",
                HttpStatus.OK
        );
    }
}
