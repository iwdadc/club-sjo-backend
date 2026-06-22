package ar.sanjoseobrero.backend.repository;

import ar.sanjoseobrero.backend.entity.UsuarioSistema;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioSistemaRepository extends JpaRepository <UsuarioSistema, Long> {
    Optional<UsuarioSistema> findByEmail(String email);

    boolean existsByEmail(String email);
}
