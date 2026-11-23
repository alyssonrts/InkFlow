
package br.edu.aesa.inkflow.dto;

import jakarta.validation.constraints.NotNull;

public record DecisaoOrcamentoRequest(
    @NotNull Boolean aprovarValor,
    @NotNull Boolean aprovarDesenho
) {}
