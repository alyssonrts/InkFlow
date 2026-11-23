package br.edu.aesa.inkflow.domain.exception;

/** Violação de regra de negócio (HTTP 422). */
public class RegraDeNegocioException extends RuntimeException {

    public RegraDeNegocioException(String mensagem) {
        super(mensagem);
    }
}
