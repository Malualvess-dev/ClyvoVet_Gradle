package br.com.fiap.clyvovet.controller.veterinario;

import br.com.fiap.clyvovet.controller.Mensagens;
import br.com.fiap.clyvovet.dto.request.LeituraColeiraRequest;
import br.com.fiap.clyvovet.dto.response.LeituraColeiraResponse;
import br.com.fiap.clyvovet.security.UsuarioLogado;
import br.com.fiap.clyvovet.service.ColeiraService;
import br.com.fiap.clyvovet.service.PetService;
import br.com.fiap.clyvovet.service.banco.ProcedimentosDoBanco;
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

/**
 * Executa pela aplicação os packages do grupo de Banco de Dados e mostra o efeito do trigger de
 * auditoria da coleira (TRG_AUDITORIA_COLEIRA).
 */
@Controller
@RequestMapping("/veterinario/banco")
public class BancoDeDadosController {

    private static final String PAGINA = "veterinario/banco";

    private final ProcedimentosDoBanco procedimentosDoBanco;
    private final ColeiraService coleiraService;
    private final PetService petService;

    public BancoDeDadosController(ProcedimentosDoBanco procedimentosDoBanco, ColeiraService coleiraService,
                                  PetService petService) {
        this.procedimentosDoBanco = procedimentosDoBanco;
        this.coleiraService = coleiraService;
        this.petService = petService;
    }

    @GetMapping
    public String painel(@AuthenticationPrincipal UsuarioLogado usuario,
                         @ModelAttribute("leitura") LeituraColeiraRequest request, Model model) {
        return exibir(usuario, model);
    }

    @PostMapping("/coleira")
    public String registrarLeitura(@AuthenticationPrincipal UsuarioLogado usuario,
                                   @Valid @ModelAttribute("leitura") LeituraColeiraRequest request,
                                   BindingResult validacao, Model model, RedirectAttributes redirecionamento) {
        if (validacao.hasErrors()) {
            return exibir(usuario, model);
        }
        LeituraColeiraResponse leitura = coleiraService.registrarLeitura(request);
        redirecionamento.addFlashAttribute(Mensagens.SUCESSO, "Leitura nº %d gravada em TB_COLEIRA (%s bpm%s)."
                .formatted(leitura.id(), leitura.batimentoBpm().toPlainString(),
                        leitura.alerta() ? ", com alerta" : ""));
        return "redirect:/veterinario/banco";
    }

    private String exibir(UsuarioLogado usuario, Model model) {
        model.addAttribute("oracleDisponivel", procedimentosDoBanco.disponivel());
        model.addAttribute("petsJson", procedimentosDoBanco.exportarPetsEmJson(usuario.getEmail()));
        model.addAttribute("relatorioColeira", procedimentosDoBanco.relatorioDaColeira(usuario.getEmail()));
        model.addAttribute("auditorias", procedimentosDoBanco.ultimasAuditoriasDaColeira());
        model.addAttribute("errosDoBanco", procedimentosDoBanco.ultimosErros());
        model.addAttribute("pets", petService.listarTodos());
        return PAGINA;
    }
}
