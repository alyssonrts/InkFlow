package br.edu.aesa.inkflow.dto;

/** Resposta de login e de cadastro: token de acesso e dados do usuário. */
public record AuthResponse(String token, String tipo, long expiraEmSegundos, UsuarioResponse usuario) {

    public static AuthResponse de(String token, long expiraEmSegundos, UsuarioResponse usuario) {
        return new AuthResponse(token, "Bearer", expiraEmSegundos, usuario);
    }
}
