package br.edu.aesa.inkflow.repository;

import br.edu.aesa.inkflow.domain.model.Usuario;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {

    Optional<Usuario> findByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCase(String email);

    @Query("select u from Usuario u where u.tatuador.id = :tatuadorId")
    Optional<Usuario> buscarPorTatuador(@Param("tatuadorId") Integer tatuadorId);
}
