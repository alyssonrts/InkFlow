
package br.edu.aesa.inkflow.service;

import br.edu.aesa.inkflow.domain.exception.*;
import br.edu.aesa.inkflow.domain.model.*;
import br.edu.aesa.inkflow.dto.*;
import br.edu.aesa.inkflow.repository.*;

import java.util.List;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrcamentoService {

    private final UsuarioRepository usuarios;
    private final TatuadorRepository tatuadores;
    private final ProjetoTatuagemRepository projetos;
    private final OrcamentoRepository orcamentos;

    public OrcamentoService(
            UsuarioRepository usuarios,
            TatuadorRepository tatuadores,
            ProjetoTatuagemRepository projetos,
            OrcamentoRepository orcamentos) {

        this.usuarios = usuarios;
        this.tatuadores = tatuadores;
        this.projetos = projetos;
        this.orcamentos = orcamentos;
    }

    private Usuario usuario(String email) {
        return usuarios.findByEmailIgnoreCase(email)
                .filter(Usuario::isAtivo)
                .orElseThrow(() ->
                        new AccessDeniedException("Usuário não autorizado"));
    }

    // ETAPA 3 - Solicitar orçamento
    @Transactional
    public PedidoOrcamentoResponse solicitar(
            String email,
            SolicitacaoOrcamentoRequest req) {

        Usuario u = usuario(email);

        if (u.getPerfil() != Perfil.CLIENTE || u.getCliente() == null) {
            throw new AccessDeniedException(
                    "Apenas clientes podem solicitar orçamento");
        }

        Tatuador t = tatuadores.findById(req.tatuadorId())
                .filter(Tatuador::isAtivo)
                .orElseThrow(() ->
                        new RecursoNaoEncontradoException(
                                "Tatuador indisponível"));

        String url = req.imagemReferenciaUrl();

        if (url != null) {
        url = url.trim();

        if (url.isBlank()) {
        url = null;
                }
        }

        if (url != null
        && !(url.startsWith("https://")
        || url.startsWith("http://")
        || url.matches(
                "/uploads/[a-f0-9]{8}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{12}\\.(jpg|png)"))) {

        throw new RegraDeNegocioException(
            "Informe uma imagem de referência válida.");
}
        ProjetoTatuagem p = projetos.save(
                new ProjetoTatuagem(
                        u.getCliente(),
                        t,
                        req.descricao().trim(),
                        req.regiaoCorpo().trim(),
                        req.tamanhoAproximado().trim(),
                        url
                )
        );

        return PedidoOrcamentoResponse.de(p, null);
    }

    // ETAPA 3 - Listar orçamentos
    @Transactional(readOnly = true)
    public List<PedidoOrcamentoResponse> listar(String email) {

        Usuario u = usuario(email);
        List<ProjetoTatuagem> lista;

        if (u.getPerfil() == Perfil.CLIENTE
                && u.getCliente() != null) {

            lista = projetos.findByClienteIdOrderByDataAberturaDesc(
                    u.getCliente().getId());

        } else if (u.getPerfil() == Perfil.TATUADOR
                && u.getTatuador() != null) {

            lista = projetos.findByTatuadorIdOrderByDataAberturaDesc(
                    u.getTatuador().getId());

        } else {
            throw new AccessDeniedException(
                    "Perfil sem permissão");
        }

        return lista.stream()
                .map(p -> PedidoOrcamentoResponse.de(
                        p,
                        orcamentos.findByProjetoId(p.getId())
                                .orElse(null)))
                .toList();
    }

    // ETAPA 3 - Emitir orçamento
    @Transactional
    public PedidoOrcamentoResponse emitir(
            String email,
            Integer id,
            EmissaoOrcamentoRequest req) {

        Usuario u = usuario(email);

        if (u.getPerfil() != Perfil.TATUADOR
                || u.getTatuador() == null) {

            throw new AccessDeniedException(
                    "Apenas tatuadores podem emitir orçamento");
        }

        ProjetoTatuagem p = projetos.findById(id)
                .orElseThrow(() ->
                        new RecursoNaoEncontradoException(
                                "Solicitação não encontrada"));

        if (!p.getTatuador().getId()
                .equals(u.getTatuador().getId())) {

            throw new AccessDeniedException(
                    "Solicitação de outro tatuador");
        }

        if (!"ORCAMENTO_SOLICITADO".equals(p.getStatus())) {
            throw new RegraDeNegocioException(
                    "Este pedido já foi respondido");
        }

        Orcamento o = orcamentos.save(
                new Orcamento(
                        p,
                        req.valorTotal(),
                        req.sessoesEstimadas(),
                        req.duracaoSessaoMinutos()
                )
        );

        p.marcarOrcamentoEmitido();

        return PedidoOrcamentoResponse.de(p, o);
    }

    // ETAPA 4 - Aprovar ou recusar orçamento e desenho
    @Transactional
    public PedidoOrcamentoResponse decidir(
            String email,
            Integer id,
            DecisaoOrcamentoRequest req) {

        Usuario u = usuario(email);

        // Somente clientes podem tomar decisões
        if (u.getPerfil() != Perfil.CLIENTE
                || u.getCliente() == null) {

            throw new AccessDeniedException(
                    "Apenas clientes podem aprovar orçamentos");
        }

        ProjetoTatuagem p = projetos.findById(id)
                .orElseThrow(() ->
                        new RecursoNaoEncontradoException(
                                "Projeto não encontrado"));

        // Verificar se o projeto pertence ao cliente
        if (!p.getCliente().getId()
                .equals(u.getCliente().getId())) {

            throw new AccessDeniedException(
                    "Este projeto pertence a outro cliente");
        }

        // Verificar se existe orçamento emitido
        Orcamento o = orcamentos.findByProjetoId(p.getId())
                .orElseThrow(() ->
                        new RegraDeNegocioException(
                                "O orçamento ainda não foi emitido"));

        // Impedir alterações após decisão definitiva
        if (o.getValorAprovado() != null
                || o.getDesenhoAprovado() != null) {

            throw new RegraDeNegocioException(
                    "Este orçamento já recebeu uma decisão");
        }

        if (p.getDesenhoFinalUrl() == null
                || p.getDesenhoFinalUrl().isBlank()) {
            throw new RegraDeNegocioException(
                    "O tatuador ainda não apresentou o desenho final");
        }

        o.decidirValor(req.aprovarValor());
        o.decidirDesenho(req.aprovarDesenho());

        if (o.estaTotalmenteAprovado()) {
            p.marcarAprovado();
        } else if (o.possuiRecusa()) {
            p.marcarRecusado();
        }
        
        return PedidoOrcamentoResponse.de(p, o);
    }

    @Transactional
    public PedidoOrcamentoResponse registrarDesenho(
            String email,
            Integer id,
            String url) {

        Usuario u = usuario(email);

        if (u.getPerfil() != Perfil.TATUADOR
                || u.getTatuador() == null) {
            throw new AccessDeniedException(
                    "Apenas tatuadores podem registrar desenhos");
        }

        ProjetoTatuagem p = projetos.findById(id)
                .orElseThrow(() ->
                        new RecursoNaoEncontradoException(
                                "Projeto não encontrado"));

        if (!p.getTatuador().getId()
                .equals(u.getTatuador().getId())) {
            throw new AccessDeniedException(
                    "Este projeto pertence a outro tatuador");
        }

        if (url != null) {
    url = url.trim();
}
                if (url == null || url.isBlank()
                || !(url.startsWith("https://")
                || url.startsWith("http://")
                || url.matches(
                "/uploads/[a-f0-9]{8}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{12}\\.(jpg|png)"))) {

                throw new RegraDeNegocioException(
            "Informe uma imagem válida para o desenho final.");
        }
        if (url.length() > 1000) {
            throw new RegraDeNegocioException(
                    "A URL do desenho é muito longa");
        }

        if (!"DESENHO_EM_APROVACAO".equals(p.getStatus())) {
            throw new RegraDeNegocioException(
                    "O projeto não está aguardando aprovação");
        }

        p.registrarDesenhoFinal(url);

        Orcamento o = orcamentos.findByProjetoId(p.getId())
                .orElseThrow(() ->
                        new RegraDeNegocioException(
                                "O orçamento ainda não foi emitido"));

        return PedidoOrcamentoResponse.de(p, o);
    }

}
