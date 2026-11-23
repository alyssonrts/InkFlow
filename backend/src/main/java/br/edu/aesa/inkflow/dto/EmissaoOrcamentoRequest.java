package br.edu.aesa.inkflow.dto;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
public record EmissaoOrcamentoRequest(
 @NotNull @DecimalMin("0.01") @Digits(integer=8,fraction=2) BigDecimal valorTotal,
 @NotNull @Min(1) Integer sessoesEstimadas,
 @NotNull @Min(1) Integer duracaoSessaoMinutos) {}
