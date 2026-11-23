package br.edu.aesa.inkflow.dto;

import jakarta.validation.constraints.NotNull;

public record SituacaoRequest(@NotNull(message = "Informe a situação desejada.") Boolean ativo) {
}
