package br.edu.aesa.inkflow.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "tatuador")
public class Tatuador {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_tatuador")
    private Integer id;

    @Column(name = "nome", nullable = false, length = 100)
    private String nome;

    @Column(name = "especialidade", length = 80)
    private String especialidade;

    @Column(name = "registro_profissional", length = 30)
    private String registroProfissional;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_sala_padrao", nullable = false)
    private Sala salaPadrao;

    @Column(name = "ativo", nullable = false)
    private boolean ativo;

    protected Tatuador() {
    }

    public Tatuador(String nome, String especialidade, String registroProfissional, Sala salaPadrao) {
        this.nome = nome;
        this.especialidade = especialidade;
        this.registroProfissional = registroProfissional;
        this.salaPadrao = salaPadrao;
        this.ativo = true;
    }

    public void atualizarDados(String nome, String especialidade, String registroProfissional, Sala salaPadrao) {
        this.nome = nome;
        this.especialidade = especialidade;
        this.registroProfissional = registroProfissional;
        this.salaPadrao = salaPadrao;
    }

    public void ativar() {
        this.ativo = true;
    }

    public void inativar() {
        this.ativo = false;
    }

    public Integer getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getEspecialidade() {
        return especialidade;
    }

    public String getRegistroProfissional() {
        return registroProfissional;
    }

    public Sala getSalaPadrao() {
        return salaPadrao;
    }

    public boolean isAtivo() {
        return ativo;
    }
}
