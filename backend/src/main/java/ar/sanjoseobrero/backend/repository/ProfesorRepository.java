package ar.sanjoseobrero.backend.repository;

import ar.sanjoseobrero.backend.entity.Profesor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProfesorRepository extends JpaRepository <Profesor, Long> {
    // Busca el profesor a partir del id del usuario logueado
    Optional<Profesor> findByUsuarioSistemaId(Long idUsuarioSistema);
}
