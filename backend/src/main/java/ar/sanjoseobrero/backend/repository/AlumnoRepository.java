// AlumnoRepository.java - Acceso a datos de Alumno

package ar.sanjoseobrero.backend.repository;

import ar.sanjoseobrero.backend.entity.Alumno;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AlumnoRepository extends JpaRepository<Alumno, Long>{
    // Spring Data JPA genera la query automáticamente a partir del nombre del método
    // findByDni → SELECT * FROM alumnos WHERE dni = ?
    Optional<Alumno> findByDni(String dni);

    // Busca alumnos por nombre o apellido (para el buscador del admin)
    List<Alumno> findByNombreContainingIgnoreCaseOrApellidoContainingIgnoreCase(
        String nombre, String apellido
    );
}
