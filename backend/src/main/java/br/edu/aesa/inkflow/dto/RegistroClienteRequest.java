package br.edu.aesa.inkflow.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

/** Dados do autocadastro de cliente (RF-01). */
public record RegistroClienteRequest(
        @NotBlank(message = "Informe o nome completo.")
        @Size(max = 100, message = "O nome deve ter no máximo 100 caracteres.") String nome,
        @NotBlank(message = "Informe o e-mail.")
        @Email(message = "Informe um e-mail válido.")
        @Size(max = 150, message = "O e-mail deve ter no máximo 150 caracteres.") String email,
        @NotBlank(message = "Informe a senha.")
        @Size(min = 8, max = 72, message = "A senha deve ter entre 8 e 72 caracteres.") String senha,
        @NotBlank(message = "Informe o telefone.")
        @Size(max = 20, message = "O telefone deve ter no máximo 20 caracteres.") String telefone,
        @NotBlank(message = "Informe o CPF.")
        @Size(max = 14, message = "O CPF deve ter no máximo 14 caracteres.") String cpf,
        @NotNull(message = "Informe a data de nascimento.")
        @Past(message = "A data de nascimento deve estar no passado.") LocalDate dataNascimento,
        boolean autorizaPortfolio) {
}
