package ar.sanjoseobrero.backend.repository;

import ar.sanjoseobrero.backend.entity.Actividad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ActividadRepository extends JpaRepository <Actividad, Long> {
    // Actividades activas
    List<Actividad> findByActivaTrue();
}
