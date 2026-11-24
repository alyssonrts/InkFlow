
package br.edu.aesa.inkflow.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "parametro_negocio")
public class ParametroNegocio {

    @Id
    @Column(name = "chave", length = 60)
    private String chave;

    @Column(name = "valor", nullable = false)
    private Integer valor;

    @Column(name = "descricao", nullable = false, length = 255)
    private String descricao;

    protected ParametroNegocio() {
    }

    public String getChave() {
        return chave;
    }

    public Integer getValor() {
        return valor;
    }

    public String getDescricao() {
        return descricao;
    }

    public void atualizarValor(Integer valor) {
        if (valor == null || valor <= 0) {
            throw new IllegalArgumentException(
                    "O valor do parâmetro deve ser maior que zero.");
        }

        this.valor = valor;
    }
}
