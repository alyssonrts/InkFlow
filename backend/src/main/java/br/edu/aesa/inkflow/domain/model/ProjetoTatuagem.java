
package br.edu.aesa.inkflow.domain.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "projeto_tatuagem")
public class ProjetoTatuagem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_projeto")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_cliente")
    private Cliente cliente;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_tatuador")
    private Tatuador tatuador;

    @Column(nullable = false, length = 255)
    private String descricao;

    @Column(name = "regiao_corpo", nullable = false, length = 60)
    private String regiaoCorpo;

    @Column(name = "tamanho_aproximado", length = 60)
    private String tamanhoAproximado;

    @Column(name = "imagem_referencia_url", length = 1000)
    private String imagemReferenciaUrl;

    @Column(name = "desenho_final_url", length = 1000)
    private String desenhoFinalUrl;

    @Column(nullable = false, length = 30)
    private String status;

    @Column(name = "data_abertura", nullable = false)
    private LocalDateTime dataAbertura;

    protected ProjetoTatuagem() {
    }

    public ProjetoTatuagem(
            Cliente cliente,
            Tatuador tatuador,
            String descricao,
            String regiaoCorpo,
            String tamanho,
            String imagem) {

        this.cliente = cliente;
        this.tatuador = tatuador;
        this.descricao = descricao;
        this.regiaoCorpo = regiaoCorpo;
        this.tamanhoAproximado = tamanho;
        this.imagemReferenciaUrl = imagem;
        this.status = "ORCAMENTO_SOLICITADO";
        this.dataAbertura = LocalDateTime.now();
    }

    // ETAPA 3 - Orçamento emitido
    public void marcarOrcamentoEmitido() {
        this.status = "DESENHO_EM_APROVACAO";
    }

    
// ETAPA 4 - Registrar o desenho final do tatuador
public void registrarDesenhoFinal(String url) {
    if (!"DESENHO_EM_APROVACAO".equals(this.status)) {
        throw new IllegalStateException(
            "O projeto não está aguardando aprovação"
        );
    }

    if (this.desenhoFinalUrl != null) {
        throw new IllegalStateException(
            "O desenho final já foi registrado"
        );
    }

    if (url == null || url.isBlank()) {
        throw new IllegalArgumentException(
            "Informe a URL do desenho final"
        );
    }

    this.desenhoFinalUrl = url.trim();
}


    // ETAPA 4 - Orçamento e desenho aprovados
    public void marcarAprovado() {
        if (!"DESENHO_EM_APROVACAO".equals(this.status)) {
            throw new IllegalStateException(
                    "O projeto não está aguardando aprovação");
        }

        this.status = "APROVADO";
    }

    // ETAPA 4 - Orçamento ou desenho recusado
    public void marcarRecusado() {
        if (!"DESENHO_EM_APROVACAO".equals(this.status)) {
            throw new IllegalStateException(
                    "O projeto não está aguardando aprovação");
        }

        this.status = "RECUSADO";
    }


        // ETAPA 5 - Confirmar agendamento da sessão
        public void marcarSessaoAgendada() {

        if (!"APROVADO".equals(this.status)
            && !"APTO_A_AGENDAR".equals(this.status)) {

        throw new IllegalStateException(
                "O projeto não está apto para agendamento");
    }

    this.status = "SESSAO_AGENDADA";
}


    public Integer getId() {
        return id;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public Tatuador getTatuador() {
        return tatuador;
    }

    public String getDescricao() {
        return descricao;
    }

    public String getRegiaoCorpo() {
        return regiaoCorpo;
    }

    public String getTamanhoAproximado() {
        return tamanhoAproximado;
    }

    public String getImagemReferenciaUrl() {
        return imagemReferenciaUrl;
    }


    public String getDesenhoFinalUrl() {
    return desenhoFinalUrl;
    }


    public String getStatus() {
        return status;
    }

    public LocalDateTime getDataAbertura() {
        return dataAbertura;
    }
}
