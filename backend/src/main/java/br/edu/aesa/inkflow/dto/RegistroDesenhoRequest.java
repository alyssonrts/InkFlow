package br.edu.aesa.inkflow.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegistroDesenhoRequest(

    @NotBlank(message = "Informe a imagem do desenho final")
    @Size(
        max = 1000,
        message = "O caminho da imagem deve ter no máximo 1000 caracteres"
    )
    String desenhoFinalUrl

) {}