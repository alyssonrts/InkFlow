package br.edu.aesa.inkflow.service;

import br.edu.aesa.inkflow.domain.exception.ConflitoException;
import br.edu.aesa.inkflow.domain.exception.CredenciaisInvalidasException;
import br.edu.aesa.inkflow.domain.exception.RecursoNaoEncontradoException;
import br.edu.aesa.inkflow.domain.model.Cliente;
import br.edu.aesa.inkflow.domain.model.Cpf;
import br.edu.aesa.inkflow.domain.model.Usuario;
import br.edu.aesa.inkflow.dto.AuthResponse;
import br.edu.aesa.inkflow.dto.LoginRequest;
import br.edu.aesa.inkflow.dto.RegistroClienteRequest;
import br.edu.aesa.inkflow.dto.UsuarioResponse;
import br.edu.aesa.inkflow.repository.ClienteRepository;
import br.edu.aesa.inkflow.repository.UsuarioRepository;
import java.util.Locale;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Casos de uso de autenticação: login, autocadastro de cliente e consulta do usuário logado. */
@Service
public class AuthService {

    private final UsuarioRepository usuarios;
    private final ClienteRepository clientes;
    private final PasswordEncoder codificadorDeSenha;
    private final JwtService jwtService;

    public AuthService(UsuarioRepository usuarios, ClienteRepository clientes,
                       PasswordEncoder codificadorDeSenha, JwtService jwtService) {
        this.usuarios = usuarios;
        this.clientes = clientes;
        this.codificadorDeSenha = codificadorDeSenha;
        this.jwtService = jwtService;
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest requisicao) {
        Usuario usuario = usuarios.findByEmailIgnoreCase(requisicao.email().trim())
                .filter(Usuario::isAtivo)
                .filter(encontrado -> codificadorDeSenha.matches(requisicao.senha(), encontrado.getSenhaHash()))
                .orElseThrow(CredenciaisInvalidasException::new);
        return montarResposta(usuario);
    }

    @Transactional
    public AuthResponse registrarCliente(RegistroClienteRequest requisicao) {
        String email = requisicao.email().trim().toLowerCase(Locale.ROOT);
        String cpf = Cpf.normalizar(requisicao.cpf());
        if (usuarios.existsByEmailIgnoreCase(email) || clientes.existsByEmailIgnoreCase(email)) {
            throw new ConflitoException("Já existe uma conta com este e-mail.");
        }
        if (clientes.existsByCpf(cpf)) {
            throw new ConflitoException("Já existe um cliente cadastrado com este CPF.");
        }
        Cliente cliente = clientes.save(new Cliente(requisicao.nome().trim(), cpf, email,
                requisicao.telefone().trim(), requisicao.dataNascimento(), requisicao.autorizaPortfolio()));
        Usuario usuario = usuarios.save(Usuario.paraCliente(email,
                codificadorDeSenha.encode(requisicao.senha()), cliente));
        return montarResposta(usuario);
    }

    @Transactional(readOnly = true)
    public UsuarioResponse usuarioLogado(String email) {
        return usuarios.findByEmailIgnoreCase(email)
                .filter(Usuario::isAtivo)
                .map(UsuarioResponse::de)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado."));
    }

    private AuthResponse montarResposta(Usuario usuario) {
        return AuthResponse.de(jwtService.gerar(usuario), jwtService.validadeEmSegundos(),
                UsuarioResponse.de(usuario));
    }
}
