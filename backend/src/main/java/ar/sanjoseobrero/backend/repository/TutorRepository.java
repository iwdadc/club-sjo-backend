// TutorRepository.java — Acceso a datos de Tutor

package ar.sanjoseobrero.backend.repository;

import ar.sanjoseobrero.backend.entity.Tutor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TutorRepository extends JpaRepository <Tutor, Long> {
     Optional<Tutor> findByDni(String dni);
}
