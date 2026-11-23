package br.edu.aesa.inkflow.repository;

import br.edu.aesa.inkflow.domain.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClienteRepository extends JpaRepository<Cliente, Integer> {

    boolean existsByCpf(String cpf);

    boolean existsByEmailIgnoreCase(String email);
}
