package br.edu.aesa.inkflow.service;

import br.edu.aesa.inkflow.dto.SalaResponse;
import br.edu.aesa.inkflow.repository.SalaRepository;
import br.edu.aesa.inkflow.repository.TatuadorRepository;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Consulta de salas, usada pelo administrador para escolher a sala exclusiva de um tatuador. */
@Service
public class SalaService {

    private final SalaRepository salas;
    private final TatuadorRepository tatuadores;

    public SalaService(SalaRepository salas, TatuadorRepository tatuadores) {
        this.salas = salas;
        this.tatuadores = tatuadores;
    }

    @Transactional(readOnly = true)
    public List<SalaResponse> listar() {
        Set<Integer> ocupadas = new HashSet<>(tatuadores.idsDasSalasOcupadas());
        return salas.findAllByOrderByNomeAsc().stream()
                .map(sala -> SalaResponse.de(sala, ocupadas.contains(sala.getId())))
                .toList();
    }
}
