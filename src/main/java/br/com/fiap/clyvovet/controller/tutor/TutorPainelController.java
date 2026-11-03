package br.com.fiap.clyvovet.controller.tutor;

import br.com.fiap.clyvovet.security.UsuarioLogado;
import br.com.fiap.clyvovet.service.ConsultaService;
import br.com.fiap.clyvovet.service.HistoricoSaudeService;
import br.com.fiap.clyvovet.service.PetService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class TutorPainelController {

    private final PetService petService;
    private final ConsultaService consultaService;
    private final HistoricoSaudeService historicoSaudeService;

    public TutorPainelController(PetService petService, ConsultaService consultaService,
                                 HistoricoSaudeService historicoSaudeService) {
        this.petService = petService;
        this.consultaService = consultaService;
        this.historicoSaudeService = historicoSaudeService;
    }

    @GetMapping("/tutor/painel")
    public String painel(@AuthenticationPrincipal UsuarioLogado usuario, Model model) {
        model.addAttribute("pets", petService.listarDoTutor(usuario.getId()));
        model.addAttribute("proximasConsultas", consultaService.proximasDoTutor(usuario.getId()));
        model.addAttribute("reforcos", historicoSaudeService.reforcosProximos(usuario.getId()));
        return "tutor/painel";
    }
}
