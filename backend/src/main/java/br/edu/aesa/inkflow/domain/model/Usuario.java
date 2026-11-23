package br.edu.aesa.inkflow.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

/**
 * Credencial de acesso ao sistema (RF-01, RF-02). Cada usuário CLIENTE aponta
 * para um cliente e cada TATUADOR para um tatuador; o ADMIN não possui vínculo.
 */
@Entity
@Table(name = "usuario")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    private Integer id;

    @Column(name = "nome", nullable = false, length = 100)
    private String nome;

    @Column(name = "email", nullable = false, unique = true, length = 150)
    private String email;

    @Column(name = "senha_hash", nullable = false, length = 100)
    private String senhaHash;

    @Enumerated(EnumType.STRING)
    @Column(name = "perfil", nullable = false, length = 20)
    private Perfil perfil;

    @Column(name = "ativo", nullable = false)
    private boolean ativo;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_cliente")
    private Cliente cliente;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_tatuador")
    private Tatuador tatuador;

    protected Usuario() {
    }

    private Usuario(String nome, String email, String senhaHash, Perfil perfil) {
        this.nome = nome;
        this.email = email;
        this.senhaHash = senhaHash;
        this.perfil = perfil;
        this.ativo = true;
    }

    public static Usuario paraCliente(String email, String senhaHash, Cliente cliente) {
        Usuario usuario = new Usuario(cliente.getNome(), email, senhaHash, Perfil.CLIENTE);
        usuario.cliente = cliente;
        return usuario;
    }

    public static Usuario paraTatuador(String email, String senhaHash, Tatuador tatuador) {
        Usuario usuario = new Usuario(tatuador.getNome(), email, senhaHash, Perfil.TATUADOR);
        usuario.tatuador = tatuador;
        return usuario;
    }

    public static Usuario paraAdministrador(String nome, String email, String senhaHash) {
        return new Usuario(nome, email, senhaHash, Perfil.ADMIN);
    }

    public void atualizarNome(String nome) {
        this.nome = nome;
    }

    public void definirAtivo(boolean ativo) {
        this.ativo = ativo;
    }

    public Integer getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getEmail() {
        return email;
    }

    public String getSenhaHash() {
        return senhaHash;
    }

    public Perfil getPerfil() {
        return perfil;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public Tatuador getTatuador() {
        return tatuador;
    }

    public boolean isAtivo() {
        return ativo;
    }
}
