package br.edu.aesa.inkflow.dto;

import br.edu.aesa.inkflow.domain.model.Usuario;

public record UsuarioResponse(Integer id, String nome, String email, String perfil) {

    public static UsuarioResponse de(Usuario usuario) {
        return new UsuarioResponse(usuario.getId(), usuario.getNome(), usuario.getEmail(),
                usuario.getPerfil().name());
    }
}
