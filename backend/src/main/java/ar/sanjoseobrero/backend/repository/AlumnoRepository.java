package ar.sanjoseobrero.backend.repository;

import ar.sanjoseobrero.backend.entity.Alumno;
import ar.sanjoseobrero.backend.entity.enums.EstadoInscripcion;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AlumnoRepository extends JpaRepository<Alumno, Long> {

    // Query 1: carga alumno + tutor + salud + pastoral con sus grupos
    // (dos colecciones tipo List no pueden ir juntas — por eso inscripciones va aparte)
    @Query("""
        SELECT DISTINCT a FROM Alumno a
        LEFT JOIN FETCH a.tutor
        LEFT JOIN FETCH a.datosSalud
        LEFT JOIN FETCH a.datosPastorales dp
        LEFT JOIN FETCH dp.gruposPastorales
        WHERE a.id = :id
    """)
    Optional<Alumno> findByIdConDatosPrincipales(@Param("id") Long id);

    // Query 2: carga las inscripciones con actividad, sede y sedes sugeridas
    // Se ejecuta después de la primera para evitar MultipleBagFetchException
    @Query("""
        SELECT DISTINCT a FROM Alumno a
        LEFT JOIN FETCH a.inscripciones i
        LEFT JOIN FETCH i.actividad
        LEFT JOIN FETCH i.sede
        LEFT JOIN FETCH i.sedesSugeridas
        WHERE a.id = :id
    """)
    Optional<Alumno> findByIdConInscripciones(@Param("id") Long id);

    // Listado general — sin inscripciones para mantenerlo liviano
    @Query("""
        SELECT DISTINCT a FROM Alumno a
        LEFT JOIN FETCH a.tutor
        LEFT JOIN FETCH a.datosSalud
        LEFT JOIN FETCH a.datosPastorales dp
        LEFT JOIN FETCH dp.gruposPastorales
    """)
    List<Alumno> findAllConRelaciones();

    Optional<Alumno> findByDni(String dni);

    List<Alumno> findByNombreContainingIgnoreCaseOrApellidoContainingIgnoreCase(
        String nombre, String apellido
    );

    // Alumnos que tienen al menos una inscripción confirmada
    @Query("""
    SELECT DISTINCT a FROM Alumno a
    LEFT JOIN FETCH a.tutor
    LEFT JOIN FETCH a.datosSalud
    LEFT JOIN FETCH a.datosPastorales dp
    LEFT JOIN FETCH dp.gruposPastorales
    JOIN a.inscripciones i
    WHERE i.estado = :estado
    """)
    List<Alumno> findByInscripcionesEstado(@Param("estado") EstadoInscripcion estado);
}