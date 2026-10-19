package br.com.fiap.clyvovet.web;

import br.com.fiap.clyvovet.security.UsuarioLogado;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PainelController {

    @GetMapping("/")
    public String inicio() {
        return "redirect:/painel";
    }

    @GetMapping("/painel")
    public String painelDoPerfil(@AuthenticationPrincipal UsuarioLogado usuario) {
        if (usuario.isTutor()) {
            return "redirect:/tutor/painel";
        }
        if (usuario.isVeterinario()) {
            return "redirect:/veterinario/painel";
        }
        return "redirect:/cadastro/perfil";
    }

    @GetMapping("/acesso-negado")
    public String acessoNegado() {
        return "acesso-negado";
    }
}
