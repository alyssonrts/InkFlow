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
 private final UsuarioRepository usuarios; private final TatuadorRepository tatuadores;
 private final ProjetoTatuagemRepository projetos; private final OrcamentoRepository orcamentos;
 public OrcamentoService(UsuarioRepository usuarios,TatuadorRepository tatuadores,ProjetoTatuagemRepository projetos,OrcamentoRepository orcamentos){this.usuarios=usuarios;this.tatuadores=tatuadores;this.projetos=projetos;this.orcamentos=orcamentos;}
 private Usuario usuario(String email){return usuarios.findByEmailIgnoreCase(email).filter(Usuario::isAtivo).orElseThrow(()->new AccessDeniedException("Usuário não autorizado"));}
 @Transactional
 public PedidoOrcamentoResponse solicitar(String email,SolicitacaoOrcamentoRequest req){
  Usuario u=usuario(email);
  if(u.getPerfil()!=Perfil.CLIENTE || u.getCliente()==null)throw new AccessDeniedException("Apenas clientes podem solicitar orçamento");
  Tatuador t=tatuadores.findById(req.tatuadorId()).filter(Tatuador::isAtivo).orElseThrow(()->new RecursoNaoEncontradoException("Tatuador indisponível"));
  String url=req.imagemReferenciaUrl();
  if(url!=null && !url.isBlank() && !(url.startsWith("https://") || url.startsWith("http://")))throw new RegraDeNegocioException("A referência deve ser uma URL HTTP(S)");
  ProjetoTatuagem p=projetos.save(new ProjetoTatuagem(u.getCliente(),t,req.descricao().trim(),req.regiaoCorpo().trim(),req.tamanhoAproximado().trim(),url));
  return PedidoOrcamentoResponse.de(p,null);
 }
 @Transactional(readOnly=true)
 public List<PedidoOrcamentoResponse> listar(String email){
  Usuario u=usuario(email);
  List<ProjetoTatuagem> lista;
  if(u.getPerfil()==Perfil.CLIENTE && u.getCliente()!=null)lista=projetos.findByClienteIdOrderByDataAberturaDesc(u.getCliente().getId());
  else if(u.getPerfil()==Perfil.TATUADOR && u.getTatuador()!=null)lista=projetos.findByTatuadorIdOrderByDataAberturaDesc(u.getTatuador().getId());
  else throw new AccessDeniedException("Perfil sem permissão");
  return lista.stream().map(p->PedidoOrcamentoResponse.de(p,orcamentos.findByProjetoId(p.getId()).orElse(null))).toList();
 }
 @Transactional
 public PedidoOrcamentoResponse emitir(String email,Integer id,EmissaoOrcamentoRequest req){
  Usuario u=usuario(email);
  if(u.getPerfil()!=Perfil.TATUADOR || u.getTatuador()==null)throw new AccessDeniedException("Apenas tatuadores podem emitir orçamento");
  ProjetoTatuagem p=projetos.findById(id).orElseThrow(()->new RecursoNaoEncontradoException("Solicitação não encontrada"));
  if(!p.getTatuador().getId().equals(u.getTatuador().getId()))throw new AccessDeniedException("Solicitação de outro tatuador");
  if(!"ORCAMENTO_SOLICITADO".equals(p.getStatus()))throw new RegraDeNegocioException("Este pedido já foi respondido");
  Orcamento o=orcamentos.save(new Orcamento(p,req.valorTotal(),req.sessoesEstimadas(),req.duracaoSessaoMinutos()));
  p.marcarOrcamentoEmitido();
  return PedidoOrcamentoResponse.de(p,o);
 }
}
