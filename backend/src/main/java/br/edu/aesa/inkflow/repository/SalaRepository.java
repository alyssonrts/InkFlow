
package br.edu.aesa.inkflow.repository;

import br.edu.aesa.inkflow.domain.model.Sala;
import jakarta.persistence.LockModeType;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SalaRepository extends JpaRepository<Sala, Integer> {

    List<Sala> findAllByOrderByNomeAsc();

    Optional<Sala> findByNome(String nome);

    // ETAPA 5 - Bloqueio para agendamentos simultâneos
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select s from Sala s where s.id = :id")
    Optional<Sala> buscarPorIdComBloqueio(
            @Param("id") Integer id);
}
