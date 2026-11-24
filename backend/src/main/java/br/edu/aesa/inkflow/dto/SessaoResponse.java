
package br.edu.aesa.inkflow.dto;

import java.time.LocalDateTime;

public record SessaoResponse(

        Integer idSessao,
        Integer idProjeto,
        Integer idCliente,
        String nomeCliente,
        Integer idTatuador,
        String nomeTatuador,
        Integer idSala,
        String nomeSala,
        String descricaoProjeto,
        LocalDateTime dataHoraInicio,
        LocalDateTime dataHoraFim,
        String status,
        boolean retoque,
        String observacoes

) {
}
