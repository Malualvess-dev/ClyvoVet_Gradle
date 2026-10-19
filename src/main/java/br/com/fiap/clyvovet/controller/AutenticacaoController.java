package br.com.fiap.clyvovet.controller;

import br.com.fiap.clyvovet.dto.request.CadastroContaRequest;
import br.com.fiap.clyvovet.model.Usuario;
import br.com.fiap.clyvovet.security.ProvedoresOAuth2;
import br.com.fiap.clyvovet.security.SessaoDoUsuario;
import br.com.fiap.clyvovet.service.ContaService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AutenticacaoController {

    private static final String PAGINA_CADASTRO = "auth/cadastro";

    private final ProvedoresOAuth2 provedores;
    private final ContaService contaService;
    private final SessaoDoUsuario sessaoDoUsuario;

    public AutenticacaoController(ProvedoresOAuth2 provedores, ContaService contaService,
                                  SessaoDoUsuario sessaoDoUsuario) {
        this.provedores = provedores;
        this.contaService = contaService;
        this.sessaoDoUsuario = sessaoDoUsuario;
    }

    @GetMapping("/login")
    public String login(Model model) {
        model.addAttribute("provedores", provedores.idsConfigurados());
        return "auth/login";
    }

    @GetMapping("/cadastro")
    public String cadastro(@ModelAttribute("conta") CadastroContaRequest request) {
        return PAGINA_CADASTRO;
    }

    @PostMapping("/cadastro")
    public String cadastrar(@Valid @ModelAttribute("conta") CadastroContaRequest request, BindingResult validacao,
                            HttpServletRequest requisicao, HttpServletResponse resposta) {
        if (validacao.hasErrors()) {
            return PAGINA_CADASTRO;
        }
        Usuario usuario = contaService.criarContaComSenha(request);
        sessaoDoUsuario.autenticar(usuario, requisicao, resposta);
        return "redirect:/cadastro/perfil";
    }
}
