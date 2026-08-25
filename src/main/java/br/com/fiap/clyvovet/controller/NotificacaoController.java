package br.com.fiap.clyvovet.controller;


import br.com.fiap.clyvovet.dto.request.NotificacaoRequest;
import br.com.fiap.clyvovet.dto.response.ApiResponse;
import br.com.fiap.clyvovet.dto.response.NotificacaoResponse;
import br.com.fiap.clyvovet.service.NotificacaoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/notificacoes")
@Tag(name = "api-notificacoes")
public class NotificacaoController {
    private final NotificacaoService notificacaoService;

    public NotificacaoController(NotificacaoService notificacaoService) {
        this.notificacaoService = notificacaoService;
    }

    @Operation(summary = "Cria uma nova notificação")
    @PostMapping
    public ResponseEntity<ApiResponse> createNotificacao(
            @Valid @RequestBody NotificacaoRequest notificacaoRequest
    ) {

        notificacaoService.create(notificacaoRequest);

        return new ResponseEntity<>(
                new ApiResponse("Notificação cadastrada com sucesso"),
                HttpStatus.CREATED
        );
    }

    @Operation(summary = "Busca notificação por id")
    @GetMapping("/{id}")
    public ResponseEntity<NotificacaoResponse> readNotificacao(@PathVariable Integer id) {
        return new ResponseEntity<>(notificacaoService.readNotificacao(id), HttpStatus.OK);
    }

    @Operation(summary = "Lista notificações")
    @GetMapping
    public ResponseEntity<Page<NotificacaoResponse>> readNotificacoes(Pageable pageable) {
        return new ResponseEntity<>(notificacaoService.read(pageable), HttpStatus.OK);
    }

    @Operation(summary = "Busca notificações por status de leitura")
    @GetMapping("/lida")
    public ResponseEntity<Page<NotificacaoResponse>> readByLida(@RequestParam String lida, Pageable pageable) {
        return new ResponseEntity<>(notificacaoService.readByLida(lida, pageable), HttpStatus.OK);
    }

    @Operation(summary = "Atualiza notificação")
    @PutMapping("/{id}")
    public ResponseEntity<NotificacaoResponse> updateNotificacao(@PathVariable Integer id, @Valid @RequestBody NotificacaoRequest notificacaoRequest) {
        return new ResponseEntity<>(notificacaoService.update(id, notificacaoRequest), HttpStatus.OK);
    }

    @Operation(summary = "Remove notificação")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteNotificacao(
            @PathVariable Integer id
    ) {

        notificacaoService.delete(id);

        return new ResponseEntity<>(
                "Notificação removida com sucesso",
                HttpStatus.OK
        );
    }
}
