package br.edu.aesa.inkflow.domain.exception;

/** Dado que conflita com um registro já existente (HTTP 409). */
public class ConflitoException extends RuntimeException {

    public ConflitoException(String mensagem) {
        super(mensagem);
    }
}
