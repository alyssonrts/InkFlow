package br.edu.aesa.inkflow.service;

import br.edu.aesa.inkflow.domain.exception.*;
import br.edu.aesa.inkflow.domain.model.*;
import br.edu.aesa.inkflow.dto.*;
import br.edu.aesa.inkflow.repository.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AgendamentoService {

    private static final ZoneId FUSO =
            ZoneId.of("America/Recife");

    private static final LocalTime ABERTURA =
            LocalTime.of(8, 0);

    private static final LocalTime FECHAMENTO =
            LocalTime.of(18, 0);

    private static final int INTERVALO_MINUTOS = 30;

    private final UsuarioRepository usuarios;
    private final ClienteRepository clientes;
    private final ProjetoTatuagemRepository projetos;
    private final OrcamentoRepository orcamentos;
    private final TatuadorRepository tatuadores;
    private final SalaRepository salas;
    private final SessaoRepository sessoes;
    private final ParametroNegocioRepository parametros;

    public AgendamentoService(
            UsuarioRepository usuarios,
            ClienteRepository clientes,
            ProjetoTatuagemRepository projetos,
            OrcamentoRepository orcamentos,
            TatuadorRepository tatuadores,
            SalaRepository salas,
            SessaoRepository sessoes,
            ParametroNegocioRepository parametros) {

        this.usuarios = usuarios;
        this.clientes = clientes;
        this.projetos = projetos;
        this.orcamentos = orcamentos;
        this.tatuadores = tatuadores;
        this.salas = salas;
        this.sessoes = sessoes;
        this.parametros = parametros;
    }

    private Usuario usuario(String email) {

        return usuarios.findByEmailIgnoreCase(email)
                .filter(Usuario::isAtivo)
                .orElseThrow(() ->
                        new AccessDeniedException(
                                "Usuário não autorizado"));
    }

    private ProjetoTatuagem buscarProjeto(Integer id) {

        return projetos.findById(id)
                .orElseThrow(() ->
                        new RecursoNaoEncontradoException(
                                "Projeto não encontrado"));
    }

    // Verificar autorização sobre o projeto
    private void validarAcesso(
            Usuario usuario,
            ProjetoTatuagem projeto) {

        if (usuario.getPerfil() == Perfil.CLIENTE
                && usuario.getCliente() != null
                && usuario.getCliente().getId()
                        .equals(projeto.getCliente().getId())) {

            return;
        }

        if (usuario.getPerfil() == Perfil.TATUADOR
                && usuario.getTatuador() != null
                && usuario.getTatuador().getId()
                        .equals(projeto.getTatuador().getId())) {

            return;
        }

        if (usuario.getPerfil() == Perfil.ADMIN) {
            return;
        }

        throw new AccessDeniedException(
                "Você não possui acesso a este projeto");
    }

    // RN-01 - Dupla aprovação
    private Orcamento validarProjeto(
            ProjetoTatuagem projeto) {

        Orcamento orcamento = orcamentos
                .findByProjetoId(projeto.getId())
                .orElseThrow(() ->
                        new RegraDeNegocioException(
                                "O projeto não possui orçamento"));

        if (!orcamento.estaTotalmenteAprovado()) {

            throw new RegraDeNegocioException(
                    "O orçamento e o desenho precisam estar aprovados");
        }

        if (!"APROVADO".equals(projeto.getStatus())
                && !"APTO_A_AGENDAR".equals(projeto.getStatus())) {

            throw new RegraDeNegocioException(
                    "O projeto não está apto para agendamento");
        }

        if (orcamento.getDuracaoSessaoMinutos() == null
                || orcamento.getDuracaoSessaoMinutos() <= 0) {

            throw new RegraDeNegocioException(
                    "A duração da sessão não foi definida");
        }

        long quantidade = sessoes
                .countByProjetoIdAndRetoqueFalseAndStatusNot(
                        projeto.getId(), "CANCELADA");

        if (quantidade >= orcamento.getSessoesEstimadas()) {

            throw new RegraDeNegocioException(
                    "O limite de sessões do orçamento foi atingido");
        }

        return orcamento;
    }

    // RN-03 - Salas exclusivas e compartilhadas
    private void validarSala(
            Tatuador tatuador,
            Sala sala) {

        if (!tatuador.isAtivo()) {

            throw new RegraDeNegocioException(
                    "O tatuador está inativo");
        }

        if (!sala.isAtiva()) {

            throw new RegraDeNegocioException(
                    "A sala está inativa");
        }

        if (sala.isExclusiva()
                && !sala.getId().equals(
                        tatuador.getSalaPadrao().getId())) {

            throw new RegraDeNegocioException(
                    "Esta sala é exclusiva de outro tatuador");
        }
    }

    // RN-04 - Prazo de cicatrização
    private LocalDate dataMinima(
            ProjetoTatuagem projeto) {

        LocalDate hoje = LocalDate.now(FUSO);

        int prazo = parametros
                .findById("PRAZO_CICATRIZACAO_DIAS")
                .orElseThrow(() ->
                        new RegraDeNegocioException(
                                "Parâmetro de cicatrização não configurado"))
                .getValor();

        LocalDate liberacao = hoje;

        List<Sessao> anteriores = sessoes
                .findByProjetoIdOrderByDataHoraInicioAsc(
                        projeto.getId());

        for (Sessao sessao : anteriores) {

            if ("CANCELADA".equals(sessao.getStatus())) {
                continue;
            }

            if (!"REALIZADA".equals(sessao.getStatus())) {

                throw new RegraDeNegocioException(
                        "Existe uma sessão ainda não concluída");
            }

            LocalDate data = sessao.getDataHoraFim()
                    .toLocalDate()
                    .plusDays(prazo);

            if (data.isAfter(liberacao)) {
                liberacao = data;
            }
        }

        return liberacao;
    }

    // RN-02 - Disponibilidade cruzada
    private boolean horarioLivre(
            Integer clienteId,
            Integer tatuadorId,
            Integer salaId,
            LocalDateTime inicio,
            LocalDateTime fim) {

        return sessoes.contarConflitosTatuador(
                        tatuadorId, inicio, fim) == 0

                && sessoes.contarConflitosSala(
                        salaId, inicio, fim) == 0

                && sessoes.contarConflitosCliente(
                        clienteId, inicio, fim) == 0;
    }

    // Consultar horários disponíveis
    @Transactional(readOnly = true)
    public List<HorarioDisponivelResponse> disponibilidade(
            String email,
            Integer projetoId,
            Integer salaId,
            LocalDate data) {

        Usuario usuario = usuario(email);

        ProjetoTatuagem projeto =
                buscarProjeto(projetoId);

        validarAcesso(usuario, projeto);

        Orcamento orcamento =
                validarProjeto(projeto);

        Tatuador tatuador = projeto.getTatuador();

        Sala sala = salas.findById(salaId)
                .orElseThrow(() ->
                        new RecursoNaoEncontradoException(
                                "Sala não encontrada"));

        validarSala(tatuador, sala);

        List<HorarioDisponivelResponse> horarios =
                new ArrayList<>();

        if (data.isBefore(dataMinima(projeto))) {
            return horarios;
        }

        LocalDateTime agora = LocalDateTime.now(FUSO);

        LocalDateTime inicio = data.atTime(ABERTURA);

        LocalDateTime fechamento =
                data.atTime(FECHAMENTO);

        int duracao =
                orcamento.getDuracaoSessaoMinutos();

        while (!inicio.plusMinutes(duracao)
                .isAfter(fechamento)) {

            LocalDateTime fim =
                    inicio.plusMinutes(duracao);

            if (inicio.isAfter(agora)
                    && horarioLivre(
                            projeto.getCliente().getId(),
                            tatuador.getId(),
                            sala.getId(),
                            inicio,
                            fim)) {

                horarios.add(
                        new HorarioDisponivelResponse(
                                inicio, fim));
            }

            inicio = inicio.plusMinutes(
                    INTERVALO_MINUTOS);
        }

        return horarios;
    }

    // Confirmar agendamento
    @Transactional
    public SessaoResponse agendar(
            String email,
            AgendamentoRequest req) {

        Usuario usuario = usuario(email);

        ProjetoTatuagem projeto =
                buscarProjeto(req.projetoId());

        validarAcesso(usuario, projeto);

        // Bloquear primeiro o cliente
        clientes.buscarPorIdComBloqueio(projeto.getCliente().getId())
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Cliente não encontrado"));

        // Bloquear depois o tatuador
        Tatuador tatuador = tatuadores
                .buscarPorIdComBloqueio(
                        projeto.getTatuador().getId())
                .orElseThrow(() ->
                        new RecursoNaoEncontradoException(
                                "Tatuador não encontrado"));

        // Bloquear por último a sala
        Sala sala = salas
                .buscarPorIdComBloqueio(req.salaId())
                .orElseThrow(() ->
                        new RecursoNaoEncontradoException(
                                "Sala não encontrada"));

        // Validar novamente após adquirir os bloqueios
        Orcamento orcamento =
                validarProjeto(projeto);

        validarSala(tatuador, sala);

        LocalDateTime inicio =
                req.dataHoraInicio();

        LocalDateTime fim = inicio.plusMinutes(
                orcamento.getDuracaoSessaoMinutos());

        if (!inicio.isAfter(LocalDateTime.now(FUSO))) {

            throw new RegraDeNegocioException(
                    "O agendamento deve ser em uma data futura");
        }

        if (inicio.toLocalDate()
                .isBefore(dataMinima(projeto))) {

            throw new RegraDeNegocioException(
                    "Aguardando o fim do período de cicatrização");
        }

        if (inicio.toLocalTime().isBefore(ABERTURA)
                || fim.isAfter(
                        inicio.toLocalDate().atTime(FECHAMENTO))
                || inicio.getMinute() % INTERVALO_MINUTOS != 0
                || inicio.getSecond() != 0
                || inicio.getNano() != 0) {

            throw new RegraDeNegocioException(
                    "Horário fora das opções de atendimento");
        }

        if (!horarioLivre(
                projeto.getCliente().getId(),
                tatuador.getId(),
                sala.getId(),
                inicio,
                fim)) {

            throw new RegraDeNegocioException(
                    "Horário indisponível para o cliente, tatuador ou sala");
        }

        String observacoes = req.observacoes();

        if (observacoes != null) {
            observacoes = observacoes.trim();

            if (observacoes.isBlank()) {
                observacoes = null;
            }
        }

        Sessao sessao = new Sessao(
                projeto,
                sala,
                inicio,
                fim,
                false,
                observacoes);

        sessoes.save(sessao);

        projeto.marcarSessaoAgendada();

        return resposta(sessao);
    }

    // Listar sessões do usuário autenticado
    @Transactional(readOnly = true)
    public List<SessaoResponse> listar(String email) {

        Usuario usuario = usuario(email);

        List<Sessao> lista;

        if (usuario.getPerfil() == Perfil.CLIENTE
                && usuario.getCliente() != null) {

            lista = sessoes
                    .findByProjetoClienteIdOrderByDataHoraInicioAsc(
                            usuario.getCliente().getId());

        } else if (usuario.getPerfil() == Perfil.TATUADOR
                && usuario.getTatuador() != null) {

            lista = sessoes
                    .findByProjetoTatuadorIdOrderByDataHoraInicioAsc(
                            usuario.getTatuador().getId());

        } else if (usuario.getPerfil() == Perfil.ADMIN) {

            lista = sessoes.findAll();

        } else {

            throw new AccessDeniedException(
                    "Perfil não autorizado");
        }

        return lista.stream()
                .map(this::resposta)
                .toList();
    }

    // Converter entidade para DTO
    private SessaoResponse resposta(Sessao sessao) {

        ProjetoTatuagem projeto = sessao.getProjeto();

        return new SessaoResponse(
                sessao.getId(),
                projeto.getId(),
                projeto.getCliente().getId(),
                projeto.getCliente().getNome(),
                projeto.getTatuador().getId(),
                projeto.getTatuador().getNome(),
                sessao.getSala().getId(),
                sessao.getSala().getNome(),
                projeto.getDescricao(),
                sessao.getDataHoraInicio(),
                sessao.getDataHoraFim(),
                sessao.getStatus(),
                sessao.isRetoque(),
                sessao.getObservacoes());
    }
}
