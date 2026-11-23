package br.edu.aesa.inkflow.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/** Dados editáveis de um tatuador já cadastrado. O acesso (e-mail e senha) não muda aqui. */
public record TatuadorAtualizacaoRequest(
        @NotBlank(message = "Informe o nome do tatuador.")
        @Size(max = 100, message = "O nome deve ter no máximo 100 caracteres.") String nome,
        @Size(max = 80, message = "A especialidade deve ter no máximo 80 caracteres.") String especialidade,
        @Size(max = 30, message = "O registro profissional deve ter no máximo 30 caracteres.") String registroProfissional,
        @NotNull(message = "Selecione a sala exclusiva.") Integer salaPadraoId) {
}
