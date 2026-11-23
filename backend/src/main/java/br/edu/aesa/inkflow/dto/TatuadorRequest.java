package br.edu.aesa.inkflow.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/** Dados para cadastrar um tatuador e criar o seu acesso (RF-03). */
public record TatuadorRequest(
        @NotBlank(message = "Informe o nome do tatuador.")
        @Size(max = 100, message = "O nome deve ter no máximo 100 caracteres.") String nome,
        @NotBlank(message = "Informe o e-mail de acesso.")
        @Email(message = "Informe um e-mail válido.")
        @Size(max = 150, message = "O e-mail deve ter no máximo 150 caracteres.") String email,
        @NotBlank(message = "Informe a senha inicial.")
        @Size(min = 8, max = 72, message = "A senha deve ter entre 8 e 72 caracteres.") String senhaInicial,
        @Size(max = 80, message = "A especialidade deve ter no máximo 80 caracteres.") String especialidade,
        @Size(max = 30, message = "O registro profissional deve ter no máximo 30 caracteres.") String registroProfissional,
        @NotNull(message = "Selecione a sala exclusiva.") Integer salaPadraoId) {
}
