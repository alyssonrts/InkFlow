package br.edu.aesa.inkflow.config;

import br.edu.aesa.inkflow.domain.model.Cliente;
import br.edu.aesa.inkflow.domain.model.Sala;
import br.edu.aesa.inkflow.domain.model.Tatuador;
import br.edu.aesa.inkflow.domain.model.TipoSala;
import br.edu.aesa.inkflow.domain.model.Usuario;
import br.edu.aesa.inkflow.repository.ClienteRepository;
import br.edu.aesa.inkflow.repository.SalaRepository;
import br.edu.aesa.inkflow.repository.TatuadorRepository;
import br.edu.aesa.inkflow.repository.UsuarioRepository;
import java.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Cria salas e as três contas de demonstração (cliente, tatuador e administrador)
 * quando ainda não existem. Serve apenas para avaliação do projeto: desligue com
 * INKFLOW_DEMONSTRACAO=false em qualquer uso real.
 */
@Component
@ConditionalOnProperty(name = "inkflow.demonstracao.habilitada", havingValue = "true")
public class DadosDeDemonstracao implements ApplicationRunner {

    private static final Logger LOG = LoggerFactory.getLogger(DadosDeDemonstracao.class);

    private final SalaRepository salas;
    private final TatuadorRepository tatuadores;
    private final ClienteRepository clientes;
    private final UsuarioRepository usuarios;
    private final PasswordEncoder codificadorDeSenha;

    public DadosDeDemonstracao(SalaRepository salas, TatuadorRepository tatuadores, ClienteRepository clientes,
                               UsuarioRepository usuarios, PasswordEncoder codificadorDeSenha) {
        this.salas = salas;
        this.tatuadores = tatuadores;
        this.clientes = clientes;
        this.usuarios = usuarios;
        this.codificadorDeSenha = codificadorDeSenha;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments argumentos) {
        criarSalas();
        criarAdministrador();
        criarTatuador();
        criarCliente();
        LOG.info("Dados de demonstração verificados (contas cliente@, tatuador@ e admin@inkflow.com).");
    }

    private void criarSalas() {
        if (salas.count() > 0) {
            return;
        }
        salas.save(new Sala("Sala 1", TipoSala.EXCLUSIVA, "Sala exclusiva"));
        salas.save(new Sala("Sala 2", TipoSala.EXCLUSIVA, "Sala exclusiva"));
        salas.save(new Sala("Sala 3", TipoSala.EXCLUSIVA, "Sala exclusiva"));
        salas.save(new Sala("Sala 4", TipoSala.COMPARTILHADA, "Sala compartilhada para peças de grande porte"));
    }

    private void criarAdministrador() {
        String email = "admin@inkflow.com";
        if (!usuarios.existsByEmailIgnoreCase(email)) {
            usuarios.save(Usuario.paraAdministrador("Ana Costa", email, codificadorDeSenha.encode("admin123")));
        }
    }

    private void criarTatuador() {
        String email = "tatuador@inkflow.com";
        if (usuarios.existsByEmailIgnoreCase(email)) {
            return;
        }
        Sala sala = salas.findByNome("Sala 1").orElse(null);
        if (sala == null || tatuadores.salaOcupada(sala.getId())) {
            LOG.warn("Tatuador de demonstração não criado: a Sala 1 não existe ou já está ocupada.");
            return;
        }
        Tatuador tatuador = tatuadores.save(new Tatuador("Rafael Lima", "Realismo", "DEMO-0001", sala));
        usuarios.save(Usuario.paraTatuador(email, codificadorDeSenha.encode("tatuador123"), tatuador));
    }

    private void criarCliente() {
        String email = "cliente@inkflow.com";
        if (usuarios.existsByEmailIgnoreCase(email) || clientes.existsByEmailIgnoreCase(email)) {
            return;
        }
        Cliente cliente = clientes.save(new Cliente("Marina Souza", "529.982.247-25", email,
                "(87) 99999-0000", LocalDate.of(1998, 3, 14), true));
        usuarios.save(Usuario.paraCliente(email, codificadorDeSenha.encode("cliente123"), cliente));
    }
}
