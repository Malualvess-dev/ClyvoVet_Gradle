package br.com.fiap.clyvovet.controller.conta;

import br.com.fiap.clyvovet.controller.Mensagens;
import br.com.fiap.clyvovet.dto.request.AnexoRequest;
import br.com.fiap.clyvovet.security.UsuarioLogado;
import br.com.fiap.clyvovet.service.AnexoService;
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

/** Documentos do usuário (TB_ANEXO): exames, laudos e carteiras de vacinação guardados por link. */
@Controller
@RequestMapping("/conta/documentos")
public class DocumentoController {

    private static final String REDIRECIONAR_PARA_LISTA = "redirect:/conta/documentos";

    private final AnexoService anexoService;

    public DocumentoController(AnexoService anexoService) {
        this.anexoService = anexoService;
    }

    @GetMapping
    public String listar(@AuthenticationPrincipal UsuarioLogado usuario,
                         @ModelAttribute("documento") AnexoRequest request, Model model) {
        return exibirLista(usuario, model);
    }

    @PostMapping
    public String adicionar(@AuthenticationPrincipal UsuarioLogado usuario,
                            @Valid @ModelAttribute("documento") AnexoRequest request, BindingResult validacao,
                            Model model, RedirectAttributes redirecionamento) {
        if (validacao.hasErrors()) {
            return exibirLista(usuario, model);
        }
        anexoService.adicionar(usuario.getId(), request);
        redirecionamento.addFlashAttribute(Mensagens.SUCESSO, "Documento adicionado.");
        return REDIRECIONAR_PARA_LISTA;
    }

    @PostMapping("/{id}/exclusao")
    public String excluir(@AuthenticationPrincipal UsuarioLogado usuario, @PathVariable Long id,
                          RedirectAttributes redirecionamento) {
        anexoService.excluir(usuario.getId(), id);
        redirecionamento.addFlashAttribute(Mensagens.SUCESSO, "Documento removido.");
        return REDIRECIONAR_PARA_LISTA;
    }

    private String exibirLista(UsuarioLogado usuario, Model model) {
        model.addAttribute("documentos", anexoService.listar(usuario.getId()));
        return "conta/documentos";
    }
}
