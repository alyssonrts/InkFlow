
package br.edu.aesa.inkflow.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public record AgendamentoRequest(

        @NotNull(message = "O projeto é obrigatório.")
        @Positive(message = "O projeto informado é inválido.")
        Integer projetoId,

        @NotNull(message = "A sala é obrigatória.")
        @Positive(message = "A sala informada é inválida.")
        Integer salaId,

        @NotNull(message = "A data e o horário são obrigatórios.")
        @Future(message = "O agendamento deve ser em uma data futura.")
        LocalDateTime dataHoraInicio,

        @Size(max = 1000, message = "As observações devem ter até 1000 caracteres.")
        String observacoes

) {
}
