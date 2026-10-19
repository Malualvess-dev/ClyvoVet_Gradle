package br.com.fiap.clyvovet.controller;

import br.com.fiap.clyvovet.dto.request.LoginRequest;
import br.com.fiap.clyvovet.dto.response.LoginResponse;
import br.com.fiap.clyvovet.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @RequestBody LoginRequest request
    ) {
        return ResponseEntity.ok(authService.login(request));
    }
}