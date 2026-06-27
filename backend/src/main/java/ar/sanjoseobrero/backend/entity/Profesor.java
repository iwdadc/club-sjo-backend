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

    //Dueña de la relación con Actividad
    @ManyToMany
    @JoinTable(
    name = "profesor_actividad",
    joinColumns = @JoinColumn(name = "profesor_id"),
    inverseJoinColumns = @JoinColumn(name = "actividad_id")
    )
    @Builder.Default
    private Set<Actividad> actividades = new HashSet<>();
    }
