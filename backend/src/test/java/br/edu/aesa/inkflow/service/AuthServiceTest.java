package br.edu.aesa.inkflow.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import br.edu.aesa.inkflow.domain.exception.ConflitoException;
import br.edu.aesa.inkflow.domain.exception.CredenciaisInvalidasException;
import br.edu.aesa.inkflow.domain.model.Usuario;
import br.edu.aesa.inkflow.dto.AuthResponse;
import br.edu.aesa.inkflow.dto.LoginRequest;
import br.edu.aesa.inkflow.dto.RegistroClienteRequest;
import br.edu.aesa.inkflow.repository.ClienteRepository;
import br.edu.aesa.inkflow.repository.UsuarioRepository;
import java.time.LocalDate;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UsuarioRepository usuarios;
    @Mock
    private ClienteRepository clientes;
    @Mock
    private JwtService jwtService;

    private final PasswordEncoder codificador = new BCryptPasswordEncoder();
    private AuthService servico;

    @BeforeEach
    void preparar() {
        servico = new AuthService(usuarios, clientes, codificador, jwtService);
    }

    private Usuario administrador() {
        return Usuario.paraAdministrador("Ana Costa", "admin@inkflow.com", codificador.encode("admin123"));
    }

    @Test
    void loginComCredenciaisValidasRetornaTokenEPerfil() {
        Usuario usuario = administrador();
        when(usuarios.findByEmailIgnoreCase("admin@inkflow.com")).thenReturn(Optional.of(usuario));
        when(jwtService.gerar(usuario)).thenReturn("token-de-teste");
        when(jwtService.validadeEmSegundos()).thenReturn(7200L);

        AuthResponse resposta = servico.login(new LoginRequest("admin@inkflow.com", "admin123"));

        assertEquals("token-de-teste", resposta.token());
        assertEquals("Bearer", resposta.tipo());
        assertEquals("ADMIN", resposta.usuario().perfil());
        assertEquals(7200L, resposta.expiraEmSegundos());
    }

    @Test
    void loginComSenhaIncorretaFalha() {
        when(usuarios.findByEmailIgnoreCase("admin@inkflow.com")).thenReturn(Optional.of(administrador()));

        assertThrows(CredenciaisInvalidasException.class,
                () -> servico.login(new LoginRequest("admin@inkflow.com", "senha-errada")));
    }

    @Test
    void loginComEmailInexistenteFalha() {
        when(usuarios.findByEmailIgnoreCase("ninguem@inkflow.com")).thenReturn(Optional.empty());

        assertThrows(CredenciaisInvalidasException.class,
                () -> servico.login(new LoginRequest("ninguem@inkflow.com", "qualquer123")));
    }

    @Test
    void loginDeUsuarioInativoFalha() {
        Usuario usuario = administrador();
        usuario.definirAtivo(false);
        when(usuarios.findByEmailIgnoreCase("admin@inkflow.com")).thenReturn(Optional.of(usuario));

        assertThrows(CredenciaisInvalidasException.class,
                () -> servico.login(new LoginRequest("admin@inkflow.com", "admin123")));
    }

    @Test
    void cadastroDeClienteComEmailJaExistenteLancaConflito() {
        when(usuarios.existsByEmailIgnoreCase("maria@email.com")).thenReturn(true);
        RegistroClienteRequest requisicao = new RegistroClienteRequest("Maria Silva", "maria@email.com",
                "senha1234", "(87) 98888-0000", "529.982.247-25", LocalDate.of(1995, 5, 10), false);

        assertThrows(ConflitoException.class, () -> servico.registrarCliente(requisicao));
    }
}
