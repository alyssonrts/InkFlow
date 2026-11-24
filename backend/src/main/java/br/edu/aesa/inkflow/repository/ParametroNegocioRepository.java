
package br.edu.aesa.inkflow.repository;

import br.edu.aesa.inkflow.domain.model.ParametroNegocio;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ParametroNegocioRepository
        extends JpaRepository<ParametroNegocio, String> {
}
