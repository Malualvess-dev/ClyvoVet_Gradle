package br.com.fiap.clyvovet.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WebController {

    // LOGIN
    @GetMapping("/login")
    public String login() {
        return "login";
    }

    // DASHBOARD
    @GetMapping("/web/dashboard")
    public String dashboard() {
        return "dashboard";
    }

    // AGENDA
    @GetMapping("/web/agenda")
    public String agenda() {
        return "agenda";
    }

    // TUTORES
    @GetMapping("/web/tutores")
    public String tutores() {
        return "tutores";
    }

    // VETERINÁRIOS
    @GetMapping("/web/veterinarios")
    public String veterinarios() {
        return "veterinarios";
    }

    // ANEXOS
    @GetMapping("/web/anexos")
    public String anexos() {
        return "anexos";
    }

    // NOTIFICAÇÕES
    @GetMapping("/web/notificacoes")
    public String notificacoes() {
        return "notificacoes";
    }
}