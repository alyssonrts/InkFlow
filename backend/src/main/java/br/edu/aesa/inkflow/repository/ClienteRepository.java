
package br.edu.aesa.inkflow.repository;

import br.edu.aesa.inkflow.domain.model.Cliente;
import jakarta.persistence.LockModeType;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ClienteRepository
        extends JpaRepository<Cliente, Integer> {

    boolean existsByCpf(String cpf);

    boolean existsByEmailIgnoreCase(String email);

    // Bloquear o cliente durante o agendamento
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
        SELECT c
        FROM Cliente c
        WHERE c.id = :id
        """)
    Optional<Cliente> buscarPorIdComBloqueio(
            @Param("id") Integer id);
}
