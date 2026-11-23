package br.edu.aesa.inkflow.domain.exception;

/** Falha de autenticação (HTTP 401). A mensagem não revela se o e-mail existe. */
public class CredenciaisInvalidasException extends RuntimeException {

    public CredenciaisInvalidasException() {
        super("E-mail ou senha inválidos.");
    }
}
