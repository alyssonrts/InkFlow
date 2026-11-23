
package br.edu.aesa.inkflow.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegistroDesenhoRequest(
    @NotBlank(message = "Informe a URL do desenho")
    @Size(max = 1000, message = "A URL deve ter no máximo 1000 caracteres")
    String desenhoFinalUrl
) {}
