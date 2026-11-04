package br.com.fiap.clyvovet.controller.api;

import br.com.fiap.clyvovet.dto.api.CadastroTutorApiRequest;
import br.com.fiap.clyvovet.dto.api.CadastroVeterinarioApiRequest;
import br.com.fiap.clyvovet.dto.api.PerfilApiResponse;
import br.com.fiap.clyvovet.security.UsuarioLogado;
import br.com.fiap.clyvovet.service.AplicativoContaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/** Login (HTTP Basic + GET /api/me) e cadastro de contas pelo aplicativo mobile. */
@RestController
@RequestMapping("/api")
public class ApiContaController {

    private final AplicativoContaService contaService;

    public ApiContaController(AplicativoContaService contaService) {
        this.contaService = contaService;
    }

    @GetMapping("/me")
    public PerfilApiResponse perfil(@AuthenticationPrincipal UsuarioLogado usuario) {
        return contaService.perfil(usuario.getId());
    }

    @PostMapping("/cadastro/tutor")
    @ResponseStatus(HttpStatus.CREATED)
    public PerfilApiResponse cadastrarTutor(@Valid @RequestBody CadastroTutorApiRequest request) {
        return contaService.cadastrarTutor(request);
    }

    @PostMapping("/cadastro/veterinario")
    @ResponseStatus(HttpStatus.CREATED)
    public PerfilApiResponse cadastrarVeterinario(@Valid @RequestBody CadastroVeterinarioApiRequest request) {
        return contaService.cadastrarVeterinario(request);
    }
}
