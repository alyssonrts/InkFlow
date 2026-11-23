package br.edu.aesa.inkflow.dto;
import jakarta.validation.constraints.*;
public record SolicitacaoOrcamentoRequest(
 @NotNull Integer tatuadorId,
 @NotBlank @Size(max=255) String descricao,
 @NotBlank @Size(max=60) String regiaoCorpo,
 @NotBlank @Size(max=60) String tamanhoAproximado,
 @Size(max=1000) String imagemReferenciaUrl) {}
