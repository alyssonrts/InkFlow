package br.edu.aesa.inkflow.repository;
import br.edu.aesa.inkflow.domain.model.ProjetoTatuagem;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
public interface ProjetoTatuagemRepository extends JpaRepository<ProjetoTatuagem,Integer> {
    List<ProjetoTatuagem> findByClienteIdOrderByDataAberturaDesc(Integer clienteId);
    List<ProjetoTatuagem> findByTatuadorIdOrderByDataAberturaDesc(Integer tatuadorId);
}
