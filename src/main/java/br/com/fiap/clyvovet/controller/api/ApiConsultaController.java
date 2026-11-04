package br.com.fiap.clyvovet.controller.api;

import br.com.fiap.clyvovet.dto.api.AtendimentoApiRequest;
import br.com.fiap.clyvovet.dto.api.CancelamentoApiRequest;
import br.com.fiap.clyvovet.dto.api.ConsultaApiRequest;
import br.com.fiap.clyvovet.dto.api.ConsultaApiResponse;
import br.com.fiap.clyvovet.dto.api.RemarcacaoApiRequest;
import br.com.fiap.clyvovet.dto.api.VeterinarioApiResponse;
import br.com.fiap.clyvovet.security.UsuarioLogado;
import br.com.fiap.clyvovet.service.AplicativoConsultaService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api")
public class ApiConsultaController {

    private final AplicativoConsultaService consultaService;

    public ApiConsultaController(AplicativoConsultaService consultaService) {
        this.consultaService = consultaService;
    }

    /** Tutor: consultas dos seus pets. Veterinário: a própria agenda. */
    @GetMapping("/consultas")
    public List<ConsultaApiResponse> listar(@AuthenticationPrincipal UsuarioLogado usuario) {
        return usuario.isTutor()
                ? consultaService.listarDoTutor(usuario.getId())
                : consultaService.listarDoVeterinario(usuario.getId());
    }

    @PostMapping("/consultas")
    @ResponseStatus(HttpStatus.CREATED)
    public ConsultaApiResponse solicitar(@AuthenticationPrincipal UsuarioLogado usuario,
                                         @Valid @RequestBody ConsultaApiRequest request) {
        return consultaService.solicitar(AcessoDoAplicativo.tutor(usuario), request);
    }

    @PostMapping("/consultas/{id}/remarcacao")
    public ConsultaApiResponse remarcar(@AuthenticationPrincipal UsuarioLogado usuario, @PathVariable Long id,
                                        @Valid @RequestBody RemarcacaoApiRequest request) {
        return consultaService.remarcar(AcessoDoAplicativo.tutor(usuario), id, request);
    }

    @PostMapping("/consultas/{id}/cancelamento")
    public ConsultaApiResponse cancelar(@AuthenticationPrincipal UsuarioLogado usuario, @PathVariable Long id,
                                        @Valid @RequestBody(required = false) CancelamentoApiRequest request) {
        String justificativa = request == null ? null : request.justificativa();
        return usuario.isTutor()
                ? consultaService.cancelarPeloTutor(usuario.getId(), id, justificativa)
                : consultaService.desmarcarPeloVeterinario(AcessoDoAplicativo.veterinario(usuario), id, justificativa);
    }

    @PostMapping("/consultas/{id}/confirmacao")
    public ConsultaApiResponse confirmar(@AuthenticationPrincipal UsuarioLogado usuario, @PathVariable Long id) {
        return consultaService.confirmar(AcessoDoAplicativo.veterinario(usuario), id);
    }

    @PostMapping("/consultas/{id}/atendimento")
    public ConsultaApiResponse finalizar(@AuthenticationPrincipal UsuarioLogado usuario, @PathVariable Long id,
                                         @Valid @RequestBody AtendimentoApiRequest request) {
        return consultaService.finalizar(AcessoDoAplicativo.veterinario(usuario), id, request);
    }

    @DeleteMapping("/consultas/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void excluir(@AuthenticationPrincipal UsuarioLogado usuario, @PathVariable Long id) {
        consultaService.excluir(AcessoDoAplicativo.tutor(usuario), id);
    }

    @GetMapping("/veterinarios")
    public List<VeterinarioApiResponse> veterinarios() {
        return consultaService.listarVeterinarios();
    }

    @GetMapping("/veterinarios/{id}/horarios")
    public List<String> horarios(@PathVariable Long id,
                                 @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data) {
        return consultaService.horariosDisponiveis(id, data);
    }
}
