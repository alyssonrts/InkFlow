package br.edu.aesa.inkflow.dto;

/**
 * Envelope uniforme de todas as respostas da API: dados, mensagem para o
 * usuário e código do erro (nulo em caso de sucesso).
 */
public record RespostaApi<T>(T data, String message, String error) {

    public static <T> RespostaApi<T> ok(T data, String message) {
        return new RespostaApi<>(data, message, null);
    }

    public static <T> RespostaApi<T> erro(String message, String codigoDoErro) {
        return new RespostaApi<>(null, message, codigoDoErro);
    }
}
