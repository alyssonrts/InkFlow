package br.edu.aesa.inkflow.dto;
import br.edu.aesa.inkflow.domain.model.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
public record PedidoOrcamentoResponse(Integer id, String clienteNome, String tatuadorNome, String descricao,
 String regiaoCorpo, String tamanhoAproximado, String imagemReferenciaUrl, String status, LocalDateTime dataAbertura,
 BigDecimal valorTotal, Integer sessoesEstimadas, Integer duracaoSessaoMinutos) {
 public static PedidoOrcamentoResponse de(ProjetoTatuagem p, Orcamento o){return new PedidoOrcamentoResponse(p.getId(),p.getCliente().getNome(),p.getTatuador().getNome(),p.getDescricao(),p.getRegiaoCorpo(),p.getTamanhoAproximado(),p.getImagemReferenciaUrl(),p.getStatus(),p.getDataAbertura(),o==null?null:o.getValorTotal(),o==null?null:o.getSessoesEstimadas(),o==null?null:o.getDuracaoSessaoMinutos());}
}
