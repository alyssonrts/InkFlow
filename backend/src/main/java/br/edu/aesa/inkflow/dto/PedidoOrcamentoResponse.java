
package br.edu.aesa.inkflow.dto;

import br.edu.aesa.inkflow.domain.model.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PedidoOrcamentoResponse(
        Integer id,
        String clienteNome,
        String tatuadorNome,
        String descricao,
        String regiaoCorpo,
        String tamanhoAproximado,
        String imagemReferenciaUrl,
        String status,
        LocalDateTime dataAbertura,

        // Etapa 3 - Dados do orçamento
        BigDecimal valorTotal,
        Integer sessoesEstimadas,
        Integer duracaoSessaoMinutos,

        // Etapa 4 - Aprovação do valor
        Boolean valorAprovado,
        LocalDateTime dataDecisaoValor,

        // Etapa 4 - Aprovação do desenho
        Boolean desenhoAprovado,
        LocalDateTime dataDecisaoDesenho,
        String desenhoFinalUrl
) {

    public static PedidoOrcamentoResponse de(
            ProjetoTatuagem p,
            Orcamento o) {

        return new PedidoOrcamentoResponse(
                p.getId(),
                p.getCliente().getNome(),
                p.getTatuador().getNome(),
                p.getDescricao(),
                p.getRegiaoCorpo(),
                p.getTamanhoAproximado(),
                p.getImagemReferenciaUrl(),
                p.getStatus(),
                p.getDataAbertura(),

                // Dados da Etapa 3
                o == null ? null : o.getValorTotal(),
                o == null ? null : o.getSessoesEstimadas(),
                o == null ? null : o.getDuracaoSessaoMinutos(),

                // Dados da Etapa 4
                o == null ? null : o.getValorAprovado(),
                o == null ? null : o.getDataDecisaoValor(),
                o == null ? null : o.getDesenhoAprovado(),
                o == null ? null : o.getDataDecisaoDesenho(),
                p.getDesenhoFinalUrl()
        );
    }
}
