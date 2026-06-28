package ar.sanjoseobrero.backend.repository;

import ar.sanjoseobrero.backend.entity.Inscripcion;
import ar.sanjoseobrero.backend.entity.enums.EstadoInscripcion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InscripcionRepository extends JpaRepository <Inscripcion, Long> {
    List<Inscripcion> findByEstado(EstadoInscripcion estado);

    // Todas las inscripciones de una actividad específica
    List<Inscripcion> findByActividadId(Long idActividad);

    // Todas las inscripciones de un alumno específico
    List<Inscripcion> findByAlumnoId(Long idAlumno);

    long countByActividad_IdAndEstado(Long actividadId, EstadoInscripcion estado);

    boolean existsByAlumno_IdAndActividad_IdAndEstado(Long idAlumno, Long idActividad, EstadoInscripcion estado);

    List<Inscripcion> findByActividad_IdAndSede_IdAndEstado(Long idActividad, Long idSede, EstadoInscripcion estado);
}
