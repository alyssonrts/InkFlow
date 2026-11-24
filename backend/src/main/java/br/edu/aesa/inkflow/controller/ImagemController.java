package br.edu.aesa.inkflow.controller;

import br.edu.aesa.inkflow.dto.RespostaApi;
import br.edu.aesa.inkflow.service.ArmazenamentoImagemService;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/imagens")
public class ImagemController {

    private final ArmazenamentoImagemService armazenamento;

    public ImagemController(
            ArmazenamentoImagemService armazenamento) {

        this.armazenamento = armazenamento;
    }

    @PostMapping(
            value = "/upload",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<RespostaApi<Map<String, String>>> upload(
            @RequestParam("arquivo") MultipartFile arquivo) {

        String nomeArquivo = armazenamento.armazenar(arquivo);

        String url = "/uploads/" + nomeArquivo;

        Map<String, String> dados = Map.of(
                "nomeArquivo", nomeArquivo,
                "url", url
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(RespostaApi.ok(
                        dados,
                        "Imagem enviada com sucesso!"
                ));
    }
}