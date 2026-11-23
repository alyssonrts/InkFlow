package br.edu.aesa.inkflow.controller;
import br.edu.aesa.inkflow.dto.*;
import br.edu.aesa.inkflow.service.OrcamentoService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/api/v1/orcamentos")
public class OrcamentoController {
 private final OrcamentoService service;
 public OrcamentoController(OrcamentoService service){this.service=service;}
 @GetMapping public RespostaApi<List<PedidoOrcamentoResponse>> listar(Authentication auth){return RespostaApi.ok(service.listar(auth.getName()),"Pedidos listados");}
 @PostMapping public ResponseEntity<RespostaApi<PedidoOrcamentoResponse>> solicitar(Authentication auth,@Valid @RequestBody SolicitacaoOrcamentoRequest req){return ResponseEntity.status(HttpStatus.CREATED).body(RespostaApi.ok(service.solicitar(auth.getName(),req),"Pedido enviado ao tatuador"));}
 @PostMapping("/{id}/emitir") public RespostaApi<PedidoOrcamentoResponse> emitir(Authentication auth,@PathVariable Integer id,@Valid @RequestBody EmissaoOrcamentoRequest req){return RespostaApi.ok(service.emitir(auth.getName(),id,req),"Orçamento emitido");}
}
