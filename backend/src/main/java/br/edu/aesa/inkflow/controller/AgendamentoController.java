
package br.edu.aesa.inkflow.controller;

import br.edu.aesa.inkflow.dto.AgendamentoRequest;
import br.edu.aesa.inkflow.dto.HorarioDisponivelResponse;
import br.edu.aesa.inkflow.dto.SessaoResponse;
import br.edu.aesa.inkflow.dto.RespostaApi;
import br.edu.aesa.inkflow.service.AgendamentoService;

import jakarta.validation.Valid;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class AgendamentoController {

    private final AgendamentoService agendamentos;

    public AgendamentoController(
            AgendamentoService agendamentos) {

        this.agendamentos = agendamentos;
    }

    // Consultar horários disponíveis
    @GetMapping("/agenda/disponibilidade")
    public RespostaApi<List<HorarioDisponivelResponse>> disponibilidade(
            Authentication authentication,

            @RequestParam("projetoId")
            Integer projetoId,

            @RequestParam("salaId")
            Integer salaId,

            @RequestParam("data")
            @DateTimeFormat(
                    iso = DateTimeFormat.ISO.DATE)
            LocalDate data) {

        return RespostaApi.ok(
                agendamentos.disponibilidade(
                        authentication.getName(),
                        projetoId,
                        salaId,
                        data),
                "Horários disponíveis consultados com sucesso");
    }

    // Confirmar agendamento
    @PostMapping("/sessoes")
    @ResponseStatus(HttpStatus.CREATED)
    public RespostaApi<SessaoResponse> agendar(
            Authentication authentication,
            @Valid @RequestBody AgendamentoRequest request) {

        return RespostaApi.ok(
                agendamentos.agendar(
                        authentication.getName(),
                        request),
                "Sessão agendada com sucesso");
    }

    // Listar sessões do usuário autenticado
    @GetMapping("/sessoes")
    public RespostaApi<List<SessaoResponse>> listar(
            Authentication authentication) {

        return RespostaApi.ok(
                agendamentos.listar(
                        authentication.getName()),
                "Sessões consultadas com sucesso");
    }
}
