package ar.sanjoseobrero.backend.repository;


import ar.sanjoseobrero.backend.entity.Asistencia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface AsistenciaRepository extends JpaRepository <Asistencia, Long>{
    // Asistencia de una actividad en una fecha específica
    List<Asistencia> findByActividadIdAndFecha(Long idActividad, LocalDate fecha);

    // Historial completo de asistencia de un alumno
    List<Asistencia> findByAlumnoId(Long idAlumno);

    // Asistencia que registró un profesor específico
    List<Asistencia> findByProfesorId(Long idProfesor);

    boolean existsByAlumnoIdAndActividadIdAndFecha(Long idAlumno, Long idActividad, LocalDate fecha);

}
