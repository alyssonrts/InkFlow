package br.edu.aesa.inkflow.controller;

import br.edu.aesa.inkflow.dto.RespostaApi;
import br.edu.aesa.inkflow.dto.SalaResponse;
import br.edu.aesa.inkflow.service.SalaService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/salas")
public class SalaController {

    private final SalaService salaService;

    public SalaController(SalaService salaService) {
        this.salaService = salaService;
    }

    @GetMapping
    public RespostaApi<List<SalaResponse>> listar() {
        return RespostaApi.ok(salaService.listar(), "Salas listadas com sucesso.");
    }
}
