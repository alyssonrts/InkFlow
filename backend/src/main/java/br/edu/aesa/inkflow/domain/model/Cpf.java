package br.edu.aesa.inkflow.domain.model;

import br.edu.aesa.inkflow.domain.exception.RegraDeNegocioException;

/** Validação e normalização de CPF para o formato 000.000.000-00. */
public final class Cpf {

    private Cpf() {
    }

    public static String normalizar(String valor) {
        String digitos = valor == null ? "" : valor.replaceAll("\\D", "");
        if (!possuiDigitosValidos(digitos)) {
            throw new RegraDeNegocioException("CPF inválido.");
        }
        return digitos.substring(0, 3) + "." + digitos.substring(3, 6) + "."
                + digitos.substring(6, 9) + "-" + digitos.substring(9);
    }

    private static boolean possuiDigitosValidos(String digitos) {
        if (digitos.length() != 11 || digitos.chars().distinct().count() == 1) {
            return false;
        }
        return calcularDigito(digitos, 9) == digitos.charAt(9) - '0'
                && calcularDigito(digitos, 10) == digitos.charAt(10) - '0';
    }

    private static int calcularDigito(String digitos, int quantidade) {
        int soma = 0;
        for (int i = 0; i < quantidade; i++) {
            soma += (digitos.charAt(i) - '0') * (quantidade + 1 - i);
        }
        int resto = (soma * 10) % 11;
        return resto == 10 ? 0 : resto;
    }
}
