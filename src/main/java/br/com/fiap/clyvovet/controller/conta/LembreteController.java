package br.com.fiap.clyvovet.controller.conta;

import br.com.fiap.clyvovet.controller.Mensagens;
import br.com.fiap.clyvovet.dto.request.LembreteRequest;
import br.com.fiap.clyvovet.security.UsuarioLogado;
import br.com.fiap.clyvovet.service.LembreteService;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/** Agenda pessoal (TB_AGENDA): CRUD completo de lembretes do usuário logado. */
@Controller
@RequestMapping("/conta/lembretes")
public class LembreteController {

    private static final String PAGINA_LISTA = "conta/lembretes";
    private static final String PAGINA_EDICAO = "conta/lembrete-edicao";
    private static final String REDIRECIONAR_PARA_LISTA = "redirect:/conta/lembretes";

    private final LembreteService lembreteService;

    public LembreteController(LembreteService lembreteService) {
        this.lembreteService = lembreteService;
    }

    @GetMapping
    public String listar(@AuthenticationPrincipal UsuarioLogado usuario,
                         @ModelAttribute("lembrete") LembreteRequest request, Model model) {
        return exibirLista(usuario, model);
    }

    @PostMapping
    public String criar(@AuthenticationPrincipal UsuarioLogado usuario,
                        @Valid @ModelAttribute("lembrete") LembreteRequest request, BindingResult validacao,
                        Model model, RedirectAttributes redirecionamento) {
        if (validacao.hasErrors()) {
            return exibirLista(usuario, model);
        }
        lembreteService.criar(usuario.getId(), request);
        redirecionamento.addFlashAttribute(Mensagens.SUCESSO, "Lembrete criado.");
        return REDIRECIONAR_PARA_LISTA;
    }

    @GetMapping("/{id}/edicao")
    public String editar(@AuthenticationPrincipal UsuarioLogado usuario, @PathVariable Long id, Model model) {
        model.addAttribute("lembreteId", id);
        model.addAttribute("lembrete", LembreteRequest.de(lembreteService.detalhar(usuario.getId(), id)));
        return PAGINA_EDICAO;
    }

    @PostMapping("/{id}")
    public String atualizar(@AuthenticationPrincipal UsuarioLogado usuario, @PathVariable Long id,
                            @Valid @ModelAttribute("lembrete") LembreteRequest request, BindingResult validacao,
                            Model model, RedirectAttributes redirecionamento) {
        if (validacao.hasErrors()) {
            model.addAttribute("lembreteId", id);
            return PAGINA_EDICAO;
        }
        lembreteService.atualizar(usuario.getId(), id, request);
        redirecionamento.addFlashAttribute(Mensagens.SUCESSO, "Lembrete atualizado.");
        return REDIRECIONAR_PARA_LISTA;
    }

    @PostMapping("/{id}/exclusao")
    public String excluir(@AuthenticationPrincipal UsuarioLogado usuario, @PathVariable Long id,
                          RedirectAttributes redirecionamento) {
        lembreteService.excluir(usuario.getId(), id);
        redirecionamento.addFlashAttribute(Mensagens.SUCESSO, "Lembrete excluído.");
        return REDIRECIONAR_PARA_LISTA;
    }

    private String exibirLista(UsuarioLogado usuario, Model model) {
        model.addAttribute("lembretes", lembreteService.listar(usuario.getId()));
        return PAGINA_LISTA;
    }
}
