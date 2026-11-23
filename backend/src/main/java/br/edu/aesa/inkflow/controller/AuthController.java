package br.edu.aesa.inkflow.controller;

import br.edu.aesa.inkflow.dto.AuthResponse;
import br.edu.aesa.inkflow.dto.LoginRequest;
import br.edu.aesa.inkflow.dto.RegistroClienteRequest;
import br.edu.aesa.inkflow.dto.RespostaApi;
import br.edu.aesa.inkflow.dto.UsuarioResponse;
import br.edu.aesa.inkflow.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public RespostaApi<AuthResponse> login(@Valid @RequestBody LoginRequest requisicao) {
        return RespostaApi.ok(authService.login(requisicao), "Login realizado com sucesso.");
    }

    @PostMapping("/registrar")
    public ResponseEntity<RespostaApi<AuthResponse>> registrar(@Valid @RequestBody RegistroClienteRequest requisicao) {
        AuthResponse resposta = authService.registrarCliente(requisicao);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(RespostaApi.ok(resposta, "Conta de cliente criada com sucesso."));
    }

    @GetMapping("/me")
    public RespostaApi<UsuarioResponse> usuarioLogado(@AuthenticationPrincipal Jwt jwt) {
        return RespostaApi.ok(authService.usuarioLogado(jwt.getSubject()), "Usuário autenticado.");
    }
}
