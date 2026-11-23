package br.edu.aesa.inkflow.dto;

import br.edu.aesa.inkflow.domain.model.Tatuador;

public record TatuadorResponse(Integer id, String nome, String especialidade, String registroProfissional,
                               Integer salaId, String salaNome, boolean ativo) {

    public static TatuadorResponse de(Tatuador tatuador) {
        return new TatuadorResponse(tatuador.getId(), tatuador.getNome(), tatuador.getEspecialidade(),
                tatuador.getRegistroProfissional(), tatuador.getSalaPadrao().getId(),
                tatuador.getSalaPadrao().getNome(), tatuador.isAtivo());
    }
}
