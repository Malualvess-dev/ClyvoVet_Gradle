package br.com.fiap.clyvovet.controller;


import br.com.fiap.clyvovet.dto.request.TutorRequest;
import br.com.fiap.clyvovet.dto.response.ApiResponse;
import br.com.fiap.clyvovet.dto.response.TutorResponse;
import br.com.fiap.clyvovet.service.TutorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/tutores")
@Tag(name = "api-tutores")
public class TutorController {

    private final TutorService tutorService;

    public TutorController(TutorService tutorService) {
        this.tutorService = tutorService;
    }

    @Operation(summary = "Cria um novo tutor")
    @PostMapping
    public ResponseEntity<ApiResponse> createTutor(
            @Valid @RequestBody TutorRequest tutorRequest
    ) {

        tutorService.create(tutorRequest);

        return new ResponseEntity<>(
                new ApiResponse("Tutor cadastrado com sucesso"),
                HttpStatus.CREATED
        );
    }

    @Operation(summary = "Busca tutor por id")
    @GetMapping("/{id}")
    public ResponseEntity<TutorResponse> readTutor(@PathVariable Integer id) {
        return new ResponseEntity<>(tutorService.readTutor(id), HttpStatus.OK);
    }

    @Operation(summary = "Lista tutores")
    @GetMapping
    public ResponseEntity<Page<TutorResponse>> readTutores(Pageable pageable) {
        return new ResponseEntity<>(tutorService.read(pageable), HttpStatus.OK);
    }

    @Operation(summary = "Busca tutor por CPF")
    @GetMapping("/cpf")
    public ResponseEntity<TutorResponse> readByCpf(@RequestParam String cpf) {
        return new ResponseEntity<>(tutorService.readByCpf(cpf), HttpStatus.OK);
    }

    @Operation(summary = "Atualiza tutor")
    @PutMapping("/{id}")
    public ResponseEntity<TutorResponse> updateTutor(@PathVariable Integer id, @Valid @RequestBody TutorRequest tutorRequest) {
        return new ResponseEntity<>(tutorService.update(id, tutorRequest), HttpStatus.OK);
    }

    @Operation(summary = "Remove tutor")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteTutor(
            @PathVariable Integer id
    ) {

        tutorService.delete(id);

        return new ResponseEntity<>(
                "Tutor removido com sucesso",
                HttpStatus.OK
        );
    }
}
