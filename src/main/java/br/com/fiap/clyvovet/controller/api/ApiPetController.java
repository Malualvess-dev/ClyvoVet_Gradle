package br.com.fiap.clyvovet.controller.api;

import br.com.fiap.clyvovet.dto.api.PetApiRequest;
import br.com.fiap.clyvovet.dto.api.PetApiResponse;
import br.com.fiap.clyvovet.dto.api.RacaApiResponse;
import br.com.fiap.clyvovet.security.UsuarioLogado;
import br.com.fiap.clyvovet.service.AplicativoPetService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ApiPetController {

    private final AplicativoPetService petService;

    public ApiPetController(AplicativoPetService petService) {
        this.petService = petService;
    }

    /** Tutor: os próprios pets. Veterinário: todos os pacientes da clínica. */
    @GetMapping("/pets")
    public List<PetApiResponse> listar(@AuthenticationPrincipal UsuarioLogado usuario) {
        return usuario.isTutor() ? petService.listarDoTutor(usuario.getId()) : petService.listarTodos();
    }

    @PostMapping("/pets")
    @ResponseStatus(HttpStatus.CREATED)
    public PetApiResponse cadastrar(@AuthenticationPrincipal UsuarioLogado usuario,
                                    @Valid @RequestBody PetApiRequest request) {
        return petService.cadastrar(AcessoDoAplicativo.tutor(usuario), request);
    }

    @PutMapping("/pets/{id}")
    public PetApiResponse atualizar(@AuthenticationPrincipal UsuarioLogado usuario, @PathVariable Long id,
                                    @Valid @RequestBody PetApiRequest request) {
        return petService.atualizar(AcessoDoAplicativo.tutor(usuario), id, request);
    }

    @DeleteMapping("/pets/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void excluir(@AuthenticationPrincipal UsuarioLogado usuario, @PathVariable Long id) {
        petService.excluir(AcessoDoAplicativo.tutor(usuario), id);
    }

    @GetMapping("/racas")
    public List<RacaApiResponse> racas() {
        return petService.listarRacas();
    }
}
