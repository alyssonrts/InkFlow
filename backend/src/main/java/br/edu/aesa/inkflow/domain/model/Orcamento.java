
package br.edu.aesa.inkflow.domain.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "orcamento")
public class Orcamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_orcamento")
    private Integer id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_projeto", unique = true)
    private ProjetoTatuagem projeto;

    @Column(name = "valor_total", nullable = false, precision = 10, scale = 2)
    private BigDecimal valorTotal;

    @Column(name = "sessoes_estimadas", nullable = false)
    private Integer sessoesEstimadas;

    @Column(name = "duracao_sessao_minutos")
    private Integer duracaoSessaoMinutos;

    // Etapa 4 - Aprovação do orçamento
    @Column(name = "valor_aprovado")
    private Boolean valorAprovado;

    @Column(name = "data_decisao_valor")
    private LocalDateTime dataDecisaoValor;

    // Etapa 4 - Aprovação do desenho
    @Column(name = "desenho_aprovado")
    private Boolean desenhoAprovado;

    @Column(name = "data_decisao_desenho")
    private LocalDateTime dataDecisaoDesenho;

    protected Orcamento() {
    }

    public Orcamento(
            ProjetoTatuagem projeto,
            BigDecimal valor,
            Integer sessoes,
            Integer duracao) {

        this.projeto = projeto;
        this.valorTotal = valor;
        this.sessoesEstimadas = sessoes;
        this.duracaoSessaoMinutos = duracao;
    }

    public Integer getId() {
        return id;
    }

    public ProjetoTatuagem getProjeto() {
        return projeto;
    }

    public BigDecimal getValorTotal() {
        return valorTotal;
    }

    public Integer getSessoesEstimadas() {
        return sessoesEstimadas;
    }

    public Integer getDuracaoSessaoMinutos() {
        return duracaoSessaoMinutos;
    }

    public Boolean getValorAprovado() {
        return valorAprovado;
    }

    public LocalDateTime getDataDecisaoValor() {
        return dataDecisaoValor;
    }

    public Boolean getDesenhoAprovado() {
        return desenhoAprovado;
    }

    public LocalDateTime getDataDecisaoDesenho() {
        return dataDecisaoDesenho;
    }

    public void decidirValor(Boolean aprovado) {
        this.valorAprovado = aprovado;
        this.dataDecisaoValor = LocalDateTime.now();
    }

    public void decidirDesenho(Boolean aprovado) {
        this.desenhoAprovado = aprovado;
        this.dataDecisaoDesenho = LocalDateTime.now();
    }

    public boolean estaTotalmenteAprovado() {
        return Boolean.TRUE.equals(valorAprovado)
                && Boolean.TRUE.equals(desenhoAprovado);
    }

    public boolean possuiRecusa() {
        return Boolean.FALSE.equals(valorAprovado)
                || Boolean.FALSE.equals(desenhoAprovado);
    }
}
