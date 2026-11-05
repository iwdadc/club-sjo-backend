package ar.sanjoseobrero.backend.repository;

import ar.sanjoseobrero.backend.entity.Sede;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SedeRepository extends JpaRepository <Sede, Long> {    
}
