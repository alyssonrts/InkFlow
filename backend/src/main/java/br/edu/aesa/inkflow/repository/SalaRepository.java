package br.edu.aesa.inkflow.repository;

import br.edu.aesa.inkflow.domain.model.Sala;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SalaRepository extends JpaRepository<Sala, Integer> {

    List<Sala> findAllByOrderByNomeAsc();

    Optional<Sala> findByNome(String nome);
}
