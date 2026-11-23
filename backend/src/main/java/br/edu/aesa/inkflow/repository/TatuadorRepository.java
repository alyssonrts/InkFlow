package br.edu.aesa.inkflow.repository;

import br.edu.aesa.inkflow.domain.model.Tatuador;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TatuadorRepository extends JpaRepository<Tatuador, Integer> {

    @Query("select t from Tatuador t join fetch t.salaPadrao order by t.nome")
    List<Tatuador> listarComSala();

    @Query("select count(t) > 0 from Tatuador t where t.salaPadrao.id = :salaId and t.ativo = true")
    boolean salaOcupada(@Param("salaId") Integer salaId);

    @Query("select count(t) > 0 from Tatuador t where t.salaPadrao.id = :salaId and t.ativo = true and t.id <> :ignorarId")
    boolean salaOcupadaPorOutro(@Param("salaId") Integer salaId, @Param("ignorarId") Integer ignorarId);

    @Query("select t.salaPadrao.id from Tatuador t where t.ativo = true")
    List<Integer> idsDasSalasOcupadas();

    boolean existsByRegistroProfissional(String registroProfissional);

    boolean existsByRegistroProfissionalAndIdNot(String registroProfissional, Integer id);
}
