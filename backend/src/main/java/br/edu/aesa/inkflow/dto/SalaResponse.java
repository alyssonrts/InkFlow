package br.edu.aesa.inkflow.dto;

import br.edu.aesa.inkflow.domain.model.Sala;

/** A sala exclusiva "ocupada" já é a sala padrão de um tatuador ativo ([RN-03]). */
public record SalaResponse(Integer id, String nome, String tipo, String descricao, boolean ativa, boolean ocupada) {

    public static SalaResponse de(Sala sala, boolean ocupada) {
        return new SalaResponse(sala.getId(), sala.getNome(), sala.getTipo().name(), sala.getDescricao(),
                sala.isAtiva(), ocupada);
    }
}
