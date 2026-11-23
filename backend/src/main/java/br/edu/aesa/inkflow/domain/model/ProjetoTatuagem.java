package br.edu.aesa.inkflow.domain.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "projeto_tatuagem")
public class ProjetoTatuagem {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_projeto") private Integer id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "id_cliente") private Cliente cliente;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "id_tatuador") private Tatuador tatuador;
    @Column(nullable = false, length = 255) private String descricao;
    @Column(name = "regiao_corpo", nullable = false, length = 60) private String regiaoCorpo;
    @Column(name = "tamanho_aproximado", length = 60) private String tamanhoAproximado;
    @Column(name = "imagem_referencia_url", length = 1000) private String imagemReferenciaUrl;
    @Column(nullable = false, length = 30) private String status;
    @Column(name = "data_abertura", nullable = false) private LocalDateTime dataAbertura;
    protected ProjetoTatuagem() {}
    public ProjetoTatuagem(Cliente cliente, Tatuador tatuador, String descricao, String regiaoCorpo, String tamanho, String imagem) {
        this.cliente=cliente; this.tatuador=tatuador; this.descricao=descricao; this.regiaoCorpo=regiaoCorpo;
        this.tamanhoAproximado=tamanho; this.imagemReferenciaUrl=imagem; this.status="ORCAMENTO_SOLICITADO";
        this.dataAbertura=LocalDateTime.now();
    }
    public void marcarOrcamentoEmitido(){this.status="DESENHO_EM_APROVACAO";}
    public Integer getId(){return id;}
    public Cliente getCliente(){return cliente;}
    public Tatuador getTatuador(){return tatuador;}
    public String getDescricao(){return descricao;}
    public String getRegiaoCorpo(){return regiaoCorpo;}
    public String getTamanhoAproximado(){return tamanhoAproximado;}
    public String getImagemReferenciaUrl(){return imagemReferenciaUrl;}
    public String getStatus(){return status;}
    public LocalDateTime getDataAbertura(){return dataAbertura;}
}
