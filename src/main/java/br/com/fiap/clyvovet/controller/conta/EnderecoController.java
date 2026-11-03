package br.com.fiap.clyvovet.controller.conta;

import br.com.fiap.clyvovet.controller.Mensagens;
import br.com.fiap.clyvovet.dto.request.EnderecoRequest;
import br.com.fiap.clyvovet.dto.response.BairroResponse;
import br.com.fiap.clyvovet.security.UsuarioLogado;
import br.com.fiap.clyvovet.service.EnderecoService;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

/** Endereço do usuário (TB_ENDERECO, ligado a bairro, cidade e estado). */
@Controller
@RequestMapping("/conta/endereco")
public class EnderecoController {

    private static final String PAGINA = "conta/endereco";

    private final EnderecoService enderecoService;

    public EnderecoController(EnderecoService enderecoService) {
        this.enderecoService = enderecoService;
    }

    @ModelAttribute("bairros")
    public List<BairroResponse> bairros() {
        return enderecoService.listarBairros();
    }

    @GetMapping
    public String exibir(@AuthenticationPrincipal UsuarioLogado usuario, Model model) {
        EnderecoRequest formulario = enderecoService.buscar(usuario.getId())
                .map(EnderecoRequest::de)
                .orElseGet(EnderecoRequest::new);
        model.addAttribute("endereco", formulario);
        return PAGINA;
    }

    @PostMapping
    public String salvar(@AuthenticationPrincipal UsuarioLogado usuario,
                         @Valid @ModelAttribute("endereco") EnderecoRequest request, BindingResult validacao,
                         RedirectAttributes redirecionamento) {
        if (validacao.hasErrors()) {
            return PAGINA;
        }
        enderecoService.salvar(usuario.getId(), request);
        redirecionamento.addFlashAttribute(Mensagens.SUCESSO, "Endereço salvo.");
        return "redirect:/conta/endereco";
    }
}
