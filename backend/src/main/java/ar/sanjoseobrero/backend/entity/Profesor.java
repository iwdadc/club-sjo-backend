// Profesor.java - Entidad para los profesores del club

package ar.sanjoseobrero.backend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table (name = "profesores")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class Profesor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Relación con el usuario que le permite loguearse
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario_sistema", nullable = false, unique = true)
    private UsuarioSistema usuarioSistema;

    // Actividades que dicta * mappedBy porque Actividad es la dueña de la relación
    @ManyToMany(mappedBy = "profesores", fetch = FetchType.LAZY)
    @Builder.Default
    private Set<Actividad> actividades = new HashSet<>();
}
