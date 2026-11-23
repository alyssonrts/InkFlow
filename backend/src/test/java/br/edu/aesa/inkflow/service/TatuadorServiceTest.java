package br.edu.aesa.inkflow.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import br.edu.aesa.inkflow.domain.exception.ConflitoException;
import br.edu.aesa.inkflow.domain.exception.RecursoNaoEncontradoException;
import br.edu.aesa.inkflow.domain.exception.RegraDeNegocioException;
import br.edu.aesa.inkflow.domain.model.Sala;
import br.edu.aesa.inkflow.domain.model.Tatuador;
import br.edu.aesa.inkflow.domain.model.TipoSala;
import br.edu.aesa.inkflow.domain.model.Usuario;
import br.edu.aesa.inkflow.dto.TatuadorRequest;
import br.edu.aesa.inkflow.dto.TatuadorResponse;
import br.edu.aesa.inkflow.repository.SalaRepository;
import br.edu.aesa.inkflow.repository.TatuadorRepository;
import br.edu.aesa.inkflow.repository.UsuarioRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class TatuadorServiceTest {

    @Mock
    private TatuadorRepository tatuadores;
    @Mock
    private SalaRepository salas;
    @Mock
    private UsuarioRepository usuarios;

    private TatuadorService servico;

    @BeforeEach
    void preparar() {
        servico = new TatuadorService(tatuadores, salas, usuarios, new BCryptPasswordEncoder());
    }

    private static TatuadorRequest requisicao(Integer salaId) {
        return new TatuadorRequest("Camila Torres", "camila@inkflow.com", "senha1234",
                "Fine line", "REG-10", salaId);
    }

    private static Sala salaComId(Integer id, TipoSala tipo) {
        Sala sala = new Sala("Sala " + id, tipo, "descricao");
        ReflectionTestUtils.setField(sala, "id", id);
        return sala;
    }

    @Test
    void cadastraTatuadorEmSalaExclusivaLivre() {
        when(salas.findById(2)).thenReturn(Optional.of(salaComId(2, TipoSala.EXCLUSIVA)));
        when(tatuadores.salaOcupada(2)).thenReturn(false);
        when(tatuadores.save(any(Tatuador.class))).thenAnswer(chamada -> chamada.getArgument(0));

        TatuadorResponse resposta = servico.criar(requisicao(2));

        assertEquals("Camila Torres", resposta.nome());
        assertEquals(2, resposta.salaId());
        assertTrue(resposta.ativo());
        verify(usuarios).save(any(Usuario.class));
    }

    @Test
    void rejeitaSalaCompartilhadaComoSalaExclusiva() {
        when(salas.findById(4)).thenReturn(Optional.of(salaComId(4, TipoSala.COMPARTILHADA)));

        assertThrows(RegraDeNegocioException.class, () -> servico.criar(requisicao(4)));
        verify(tatuadores, never()).save(any(Tatuador.class));
    }

    @Test
    void rejeitaSalaExclusivaJaVinculadaAOutroTatuador() {
        when(salas.findById(1)).thenReturn(Optional.of(salaComId(1, TipoSala.EXCLUSIVA)));
        when(tatuadores.salaOcupada(1)).thenReturn(true);

        RegraDeNegocioException erro = assertThrows(RegraDeNegocioException.class,
                () -> servico.criar(requisicao(1)));

        assertTrue(erro.getMessage().contains("RN-03"));
        verify(tatuadores, never()).save(any(Tatuador.class));
    }

    @Test
    void rejeitaSalaInexistente() {
        when(salas.findById(99)).thenReturn(Optional.empty());

        assertThrows(RecursoNaoEncontradoException.class, () -> servico.criar(requisicao(99)));
    }

    @Test
    void rejeitaEmailJaCadastrado() {
        when(usuarios.existsByEmailIgnoreCase("camila@inkflow.com")).thenReturn(true);

        assertThrows(ConflitoException.class, () -> servico.criar(requisicao(2)));
        verify(tatuadores, never()).save(any(Tatuador.class));
    }

    @Test
    void inativarTatuadorBloqueiaOAcessoDoUsuario() {
        Tatuador tatuador = new Tatuador("Camila Torres", "Fine line", "REG-10",
                salaComId(2, TipoSala.EXCLUSIVA));
        Usuario usuario = Usuario.paraTatuador("camila@inkflow.com", "hash", tatuador);
        when(tatuadores.findById(5)).thenReturn(Optional.of(tatuador));
        when(usuarios.buscarPorTatuador(5)).thenReturn(Optional.of(usuario));

        TatuadorResponse resposta = servico.alterarSituacao(5, false);

        assertFalse(resposta.ativo());
        assertFalse(usuario.isAtivo());
    }

    @Test
    void reativarTatuadorExigeQueASalaContinueLivre() {
        Sala sala = salaComId(3, TipoSala.EXCLUSIVA);
        Tatuador tatuador = new Tatuador("Diego Rocha", "Blackwork", null, sala);
        tatuador.inativar();
        when(tatuadores.findById(5)).thenReturn(Optional.of(tatuador));
        when(salas.findById(3)).thenReturn(Optional.of(sala));
        when(tatuadores.salaOcupadaPorOutro(3, 5)).thenReturn(true);

        assertThrows(RegraDeNegocioException.class, () -> servico.alterarSituacao(5, true));
        assertFalse(tatuador.isAtivo());
    }
}
