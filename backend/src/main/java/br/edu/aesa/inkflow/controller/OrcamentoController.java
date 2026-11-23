
package br.edu.aesa.inkflow.controller;

import br.edu.aesa.inkflow.dto.*;
import br.edu.aesa.inkflow.service.OrcamentoService;

import jakarta.validation.Valid;
import java.util.List;

import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/orcamentos")
public class OrcamentoController {

    private final OrcamentoService service;

    public OrcamentoController(OrcamentoService service) {
        this.service = service;
    }

    // ETAPA 3 - Listar orçamentos
    @GetMapping
    public RespostaApi<List<PedidoOrcamentoResponse>> listar(
            Authentication auth) {

        return RespostaApi.ok(
                service.listar(auth.getName()),
                "Pedidos listados"
        );
    }

    // ETAPA 3 - Solicitar orçamento
    @PostMapping
    public ResponseEntity<RespostaApi<PedidoOrcamentoResponse>> solicitar(
            Authentication auth,
            @Valid @RequestBody SolicitacaoOrcamentoRequest req) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(RespostaApi.ok(
                        service.solicitar(auth.getName(), req),
                        "Pedido enviado ao tatuador"
                ));
    }

    // ETAPA 3 - Emitir orçamento
    @PostMapping("/{id}/emitir")
    public RespostaApi<PedidoOrcamentoResponse> emitir(
            Authentication auth,
            @PathVariable Integer id,
            @Valid @RequestBody EmissaoOrcamentoRequest req) {

        return RespostaApi.ok(
                service.emitir(auth.getName(), id, req),
                "Orçamento emitido"
        );
    }

    // ETAPA 4 - Aprovar ou recusar orçamento e desenho
    @PostMapping("/{id}/decidir")
    public RespostaApi<PedidoOrcamentoResponse> decidir(
            Authentication auth,
            @PathVariable Integer id,
            @Valid @RequestBody DecisaoOrcamentoRequest req) {

        return RespostaApi.ok(
                service.decidir(auth.getName(), id, req),
                "Decisão registrada com sucesso"
        );
    }

// ETAPA 4 - Tatuador apresenta o desenho final
@PostMapping("/{id}/desenho")
public RespostaApi<PedidoOrcamentoResponse> registrarDesenho(
        Authentication auth,
        @PathVariable Integer id,
        @Valid @RequestBody RegistroDesenhoRequest req) {

    return RespostaApi.ok(
            service.registrarDesenho(
                    auth.getName(),
                    id,
                    req.desenhoFinalUrl()
            ),
            "Desenho final registrado com sucesso"
    );
}

}
