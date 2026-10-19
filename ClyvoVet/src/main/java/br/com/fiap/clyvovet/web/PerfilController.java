package br.com.fiap.clyvovet.web;

import br.com.fiap.clyvovet.dto.PerfilTutorForm;
import br.com.fiap.clyvovet.dto.PerfilVeterinarioForm;
import br.com.fiap.clyvovet.model.Usuario;
import br.com.fiap.clyvovet.security.SessaoDoUsuario;
import br.com.fiap.clyvovet.security.UsuarioLogado;
import br.com.fiap.clyvovet.service.TutorService;
import br.com.fiap.clyvovet.service.VeterinarioService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/cadastro/perfil")
public class PerfilController {

    private static final String PAGINA = "auth/perfil";

    private final TutorService tutorService;
    private final VeterinarioService veterinarioService;
    private final SessaoDoUsuario sessaoDoUsuario;

    public PerfilController(TutorService tutorService, VeterinarioService veterinarioService,
                            SessaoDoUsuario sessaoDoUsuario) {
        this.tutorService = tutorService;
        this.veterinarioService = veterinarioService;
        this.sessaoDoUsuario = sessaoDoUsuario;
    }

    @ModelAttribute("tutor")
    public PerfilTutorForm formularioTutor() {
        return new PerfilTutorForm();
    }

    @ModelAttribute("veterinario")
    public PerfilVeterinarioForm formularioVeterinario() {
        return new PerfilVeterinarioForm();
    }

    @GetMapping
    public String escolherPerfil() {
        return PAGINA;
    }

    @PostMapping("/tutor")
    public String tornarTutor(@AuthenticationPrincipal UsuarioLogado usuario,
                              @Valid @ModelAttribute("tutor") PerfilTutorForm form, BindingResult validacao,
                              Model model, HttpServletRequest requisicao, HttpServletResponse resposta) {
        if (validacao.hasErrors()) {
            return reexibir(model, "tutor");
        }
        Usuario atualizado = tutorService.tornarTutor(usuario.getId(), form);
        return concluir(atualizado, requisicao, resposta);
    }

    @PostMapping("/veterinario")
    public String tornarVeterinario(@AuthenticationPrincipal UsuarioLogado usuario,
                                    @Valid @ModelAttribute("veterinario") PerfilVeterinarioForm form,
                                    BindingResult validacao, Model model,
                                    HttpServletRequest requisicao, HttpServletResponse resposta) {
        if (validacao.hasErrors()) {
            return reexibir(model, "veterinario");
        }
        Usuario atualizado = veterinarioService.tornarVeterinario(usuario.getId(), form);
        return concluir(atualizado, requisicao, resposta);
    }

    private String reexibir(Model model, String perfilEscolhido) {
        model.addAttribute("perfilEscolhido", perfilEscolhido);
        return PAGINA;
    }

    private String concluir(Usuario usuario, HttpServletRequest requisicao, HttpServletResponse resposta) {
        sessaoDoUsuario.autenticar(usuario, requisicao, resposta);
        return "redirect:/painel";
    }
}
