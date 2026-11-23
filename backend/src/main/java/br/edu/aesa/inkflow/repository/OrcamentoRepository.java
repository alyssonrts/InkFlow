package br.edu.aesa.inkflow.repository;
import br.edu.aesa.inkflow.domain.model.Orcamento;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
public interface OrcamentoRepository extends JpaRepository<Orcamento,Integer> { Optional<Orcamento> findByProjetoId(Integer projetoId); }
