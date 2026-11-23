package br.edu.aesa.inkflow.domain.model;
import jakarta.persistence.*;
import java.math.BigDecimal;
@Entity @Table(name="orcamento")
public class Orcamento {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) @Column(name="id_orcamento") private Integer id;
    @OneToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="id_projeto",unique=true) private ProjetoTatuagem projeto;
    @Column(name="valor_total",nullable=false,precision=10,scale=2) private BigDecimal valorTotal;
    @Column(name="sessoes_estimadas",nullable=false) private Integer sessoesEstimadas;
    @Column(name="duracao_sessao_minutos") private Integer duracaoSessaoMinutos;
    protected Orcamento(){}
    public Orcamento(ProjetoTatuagem projeto, BigDecimal valor, Integer sessoes, Integer duracao){this.projeto=projeto;this.valorTotal=valor;this.sessoesEstimadas=sessoes;this.duracaoSessaoMinutos=duracao;}
    public BigDecimal getValorTotal(){return valorTotal;}
    public Integer getSessoesEstimadas(){return sessoesEstimadas;}
    public Integer getDuracaoSessaoMinutos(){return duracaoSessaoMinutos;}
}
