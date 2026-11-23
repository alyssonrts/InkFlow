package br.edu.aesa.inkflow.service;

import br.edu.aesa.inkflow.domain.exception.ConflitoException;
import br.edu.aesa.inkflow.domain.exception.RecursoNaoEncontradoException;
import br.edu.aesa.inkflow.domain.exception.RegraDeNegocioException;
import br.edu.aesa.inkflow.domain.model.Sala;
import br.edu.aesa.inkflow.domain.model.Tatuador;
import br.edu.aesa.inkflow.domain.model.Usuario;
import br.edu.aesa.inkflow.dto.TatuadorAtualizacaoRequest;
import br.edu.aesa.inkflow.dto.TatuadorRequest;
import br.edu.aesa.inkflow.dto.TatuadorResponse;
import br.edu.aesa.inkflow.repository.SalaRepository;
import br.edu.aesa.inkflow.repository.TatuadorRepository;
import br.edu.aesa.inkflow.repository.UsuarioRepository;
import java.util.List;
import java.util.Locale;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Cadastro de tatuadores pelo administrador ([RF-03]) com a regra de sala exclusiva ([RN-03]). */
@Service
public class TatuadorService {

    private final TatuadorRepository tatuadores;
    private final SalaRepository salas;
    private final UsuarioRepository usuarios;
    private final PasswordEncoder codificadorDeSenha;

    public TatuadorService(TatuadorRepository tatuadores, SalaRepository salas,
                           UsuarioRepository usuarios, PasswordEncoder codificadorDeSenha) {
        this.tatuadores = tatuadores;
        this.salas = salas;
        this.usuarios = usuarios;
        this.codificadorDeSenha = codificadorDeSenha;
    }

    @Transactional(readOnly = true)
    public List<TatuadorResponse> listar() {
        return tatuadores.listarComSala().stream().map(TatuadorResponse::de).toList();
    }

    @Transactional(readOnly = true)
    public TatuadorResponse buscar(Integer id) {
        return TatuadorResponse.de(obterTatuador(id));
    }

    @Transactional
    public TatuadorResponse criar(TatuadorRequest requisicao) {
        String email = requisicao.email().trim().toLowerCase(Locale.ROOT);
        String registro = textoOuNulo(requisicao.registroProfissional());
        if (usuarios.existsByEmailIgnoreCase(email)) {
            throw new ConflitoException("Já existe uma conta com este e-mail.");
        }
        if (registro != null && tatuadores.existsByRegistroProfissional(registro)) {
            throw new ConflitoException("Já existe um tatuador com este registro profissional.");
        }
        Sala sala = obterSalaExclusivaLivre(requisicao.salaPadraoId(), null);
        Tatuador tatuador = tatuadores.save(new Tatuador(requisicao.nome().trim(),
                textoOuNulo(requisicao.especialidade()), registro, sala));
        usuarios.save(Usuario.paraTatuador(email, codificadorDeSenha.encode(requisicao.senhaInicial()), tatuador));
        return TatuadorResponse.de(tatuador);
    }

    @Transactional
    public TatuadorResponse atualizar(Integer id, TatuadorAtualizacaoRequest requisicao) {
        Tatuador tatuador = obterTatuador(id);
        String registro = textoOuNulo(requisicao.registroProfissional());
        if (registro != null && tatuadores.existsByRegistroProfissionalAndIdNot(registro, id)) {
            throw new ConflitoException("Já existe um tatuador com este registro profissional.");
        }
        Sala sala = tatuador.isAtivo()
                ? obterSalaExclusivaLivre(requisicao.salaPadraoId(), id)
                : obterSala(requisicao.salaPadraoId());
        tatuador.atualizarDados(requisicao.nome().trim(), textoOuNulo(requisicao.especialidade()), registro, sala);
        usuarios.buscarPorTatuador(id).ifPresent(usuario -> usuario.atualizarNome(tatuador.getNome()));
        return TatuadorResponse.de(tatuador);
    }

    /** Inativar bloqueia o login do tatuador e libera a sala exclusiva; reativar exige a sala livre. */
    @Transactional
    public TatuadorResponse alterarSituacao(Integer id, boolean ativo) {
        Tatuador tatuador = obterTatuador(id);
        if (ativo) {
            obterSalaExclusivaLivre(tatuador.getSalaPadrao().getId(), id);
            tatuador.ativar();
        } else {
            tatuador.inativar();
        }
        usuarios.buscarPorTatuador(id).ifPresent(usuario -> usuario.definirAtivo(ativo));
        return TatuadorResponse.de(tatuador);
    }

    private Tatuador obterTatuador(Integer id) {
        return tatuadores.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Tatuador não encontrado."));
    }

    private Sala obterSala(Integer id) {
        return salas.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Sala não encontrada."));
    }

    private Sala obterSalaExclusivaLivre(Integer salaId, Integer tatuadorIgnorado) {
        Sala sala = obterSala(salaId);
        if (!sala.isAtiva()) {
            throw new RegraDeNegocioException("A sala selecionada está inativa.");
        }
        if (!sala.isExclusiva()) {
            throw new RegraDeNegocioException(
                    "Selecione uma sala exclusiva. As salas compartilhadas ficam disponíveis a todos os tatuadores [RN-03].");
        }
        boolean ocupada = tatuadorIgnorado == null
                ? tatuadores.salaOcupada(salaId)
                : tatuadores.salaOcupadaPorOutro(salaId, tatuadorIgnorado);
        if (ocupada) {
            throw new RegraDeNegocioException("Esta sala exclusiva já está vinculada a outro tatuador ativo [RN-03].");
        }
        return sala;
    }

    private static String textoOuNulo(String texto) {
        if (texto == null || texto.isBlank()) {
            return null;
        }
        return texto.trim();
    }
}
