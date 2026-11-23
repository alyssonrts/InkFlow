package br.edu.aesa.inkflow.controller;

import br.edu.aesa.inkflow.dto.RespostaApi;
import br.edu.aesa.inkflow.dto.SituacaoRequest;
import br.edu.aesa.inkflow.dto.TatuadorAtualizacaoRequest;
import br.edu.aesa.inkflow.dto.TatuadorRequest;
import br.edu.aesa.inkflow.dto.TatuadorResponse;
import br.edu.aesa.inkflow.service.TatuadorService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Leitura liberada a qualquer usuário autenticado; escrita somente para o perfil ADMIN (ver SecurityConfig). */
@RestController
@RequestMapping("/api/v1/tatuadores")
public class TatuadorController {

    private final TatuadorService tatuadorService;

    public TatuadorController(TatuadorService tatuadorService) {
        this.tatuadorService = tatuadorService;
    }

    @GetMapping
    public RespostaApi<List<TatuadorResponse>> listar() {
        return RespostaApi.ok(tatuadorService.listar(), "Tatuadores listados com sucesso.");
    }

    @GetMapping("/{id}")
    public RespostaApi<TatuadorResponse> buscar(@PathVariable Integer id) {
        return RespostaApi.ok(tatuadorService.buscar(id), "Tatuador encontrado.");
    }

    @PostMapping
    public ResponseEntity<RespostaApi<TatuadorResponse>> criar(@Valid @RequestBody TatuadorRequest requisicao) {
        TatuadorResponse criado = tatuadorService.criar(requisicao);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(RespostaApi.ok(criado, "Tatuador cadastrado com sucesso."));
    }

    @PutMapping("/{id}")
    public RespostaApi<TatuadorResponse> atualizar(@PathVariable Integer id,
                                                   @Valid @RequestBody TatuadorAtualizacaoRequest requisicao) {
        return RespostaApi.ok(tatuadorService.atualizar(id, requisicao), "Tatuador atualizado com sucesso.");
    }

    @PatchMapping("/{id}/situacao")
    public RespostaApi<TatuadorResponse> alterarSituacao(@PathVariable Integer id,
                                                         @Valid @RequestBody SituacaoRequest requisicao) {
        return RespostaApi.ok(tatuadorService.alterarSituacao(id, requisicao.ativo()),
                "Situação do tatuador atualizada.");
    }
}
