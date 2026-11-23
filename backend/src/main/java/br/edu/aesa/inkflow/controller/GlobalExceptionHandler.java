package br.edu.aesa.inkflow.controller;

import br.edu.aesa.inkflow.domain.exception.ConflitoException;
import br.edu.aesa.inkflow.domain.exception.CredenciaisInvalidasException;
import br.edu.aesa.inkflow.domain.exception.RecursoNaoEncontradoException;
import br.edu.aesa.inkflow.domain.exception.RegraDeNegocioException;
import br.edu.aesa.inkflow.dto.RespostaApi;
import java.util.LinkedHashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Manipulador global de exceções: converte as falhas no envelope padrão da API
 * e nunca devolve o rastreamento de pilha ao cliente.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger LOG = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(RegraDeNegocioException.class)
    public ResponseEntity<RespostaApi<Object>> regraDeNegocio(RegraDeNegocioException excecao) {
        return resposta(HttpStatus.UNPROCESSABLE_ENTITY, excecao.getMessage(), "REGRA_DE_NEGOCIO");
    }

    @ExceptionHandler(RecursoNaoEncontradoException.class)
    public ResponseEntity<RespostaApi<Object>> naoEncontrado(RecursoNaoEncontradoException excecao) {
        return resposta(HttpStatus.NOT_FOUND, excecao.getMessage(), "NAO_ENCONTRADO");
    }

    @ExceptionHandler(ConflitoException.class)
    public ResponseEntity<RespostaApi<Object>> conflito(ConflitoException excecao) {
        return resposta(HttpStatus.CONFLICT, excecao.getMessage(), "CONFLITO");
    }

    @ExceptionHandler(CredenciaisInvalidasException.class)
    public ResponseEntity<RespostaApi<Object>> credenciaisInvalidas(CredenciaisInvalidasException excecao) {
        return resposta(HttpStatus.UNAUTHORIZED, excecao.getMessage(), "CREDENCIAIS_INVALIDAS");
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<RespostaApi<Object>> dadosInvalidos(MethodArgumentNotValidException excecao) {
        Map<String, String> campos = new LinkedHashMap<>();
        for (FieldError erro : excecao.getBindingResult().getFieldErrors()) {
            campos.putIfAbsent(erro.getField(), erro.getDefaultMessage());
        }
        RespostaApi<Object> corpo = new RespostaApi<>(Map.of("campos", campos),
                "Dados inválidos. Revise os campos informados.", "VALIDACAO");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(corpo);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<RespostaApi<Object>> corpoIlegivel(HttpMessageNotReadableException excecao) {
        return resposta(HttpStatus.BAD_REQUEST, "Corpo da requisição inválido ou mal formatado.", "REQUISICAO_INVALIDA");
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<RespostaApi<Object>> integridade(DataIntegrityViolationException excecao) {
        LOG.warn("Violação de integridade no banco de dados", excecao);
        return resposta(HttpStatus.CONFLICT, "A operação conflita com dados já existentes.", "CONFLITO");
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<RespostaApi<Object>> inesperado(Exception excecao) {
        if (excecao instanceof ErrorResponse erroDoFramework) {
            return resposta(HttpStatus.valueOf(erroDoFramework.getStatusCode().value()),
                    "Requisição não atendida.", "REQUISICAO_INVALIDA");
        }
        LOG.error("Erro inesperado", excecao);
        return resposta(HttpStatus.INTERNAL_SERVER_ERROR,
                "Ocorreu um erro inesperado. Tente novamente mais tarde.", "ERRO_INTERNO");
    }

    private static ResponseEntity<RespostaApi<Object>> resposta(HttpStatus status, String mensagem, String codigo) {
        return ResponseEntity.status(status).body(RespostaApi.erro(mensagem, codigo));
    }
}
