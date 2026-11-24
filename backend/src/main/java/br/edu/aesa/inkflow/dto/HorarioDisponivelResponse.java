
package br.edu.aesa.inkflow.dto;

import java.time.LocalDateTime;

public record HorarioDisponivelResponse(

        LocalDateTime dataHoraInicio,
        LocalDateTime dataHoraFim

) {
}
