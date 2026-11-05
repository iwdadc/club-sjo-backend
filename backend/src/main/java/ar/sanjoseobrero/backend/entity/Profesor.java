// Profesor.java - Entidad para los profesores del club

package ar.sanjoseobrero.backend.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;


import java.util.HashSet;
import java.util.Set;

@Entity
@Table (name = "profesores")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder

public class Profesor extends Persona {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Relación con el usuario que le permite loguearse
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario_sistema", nullable = false, unique = true)
    private UsuarioSistema usuarioSistema;

    // Cada Asignacion representa profesor+actividad+sede juntos
    // Un profesor puede dar la misma actividad en sedes distintas.
    @OneToMany(mappedBy = "profesor", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<Asignacion> asignaciones = new HashSet<>();

}
