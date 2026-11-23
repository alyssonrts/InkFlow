package br.edu.aesa.inkflow.domain.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import br.edu.aesa.inkflow.domain.exception.RegraDeNegocioException;
import org.junit.jupiter.api.Test;

class CpfTest {

    @Test
    void formataCpfValidoInformadoSomenteComDigitos() {
        assertEquals("529.982.247-25", Cpf.normalizar("52998224725"));
    }

    @Test
    void aceitaCpfJaFormatado() {
        assertEquals("529.982.247-25", Cpf.normalizar("529.982.247-25"));
    }

    @Test
    void rejeitaDigitosVerificadoresIncorretos() {
        assertThrows(RegraDeNegocioException.class, () -> Cpf.normalizar("529.982.247-26"));
    }

    @Test
    void rejeitaSequenciaDeDigitosRepetidos() {
        assertThrows(RegraDeNegocioException.class, () -> Cpf.normalizar("111.111.111-11"));
    }

    @Test
    void rejeitaTamanhoInvalidoOuValorNulo() {
        assertThrows(RegraDeNegocioException.class, () -> Cpf.normalizar("123"));
        assertThrows(RegraDeNegocioException.class, () -> Cpf.normalizar(null));
    }
}
