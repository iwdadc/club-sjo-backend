// Asignacion.java - Representa que un profesor da una actividad puntual en una sede puntual.

package ar.sanjoseobrero.backend.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
    name = "asignaciones",
    uniqueConstraints = @UniqueConstraint(columnNames = {"id_profesor", "id_actividad", "id_sede"})
    // Evita que la misma combinación profesor+actividad+sede se duplique por error
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Asignacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_profesor", nullable = false)
    private Profesor profesor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_actividad", nullable = false)
    private Actividad actividad;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_sede", nullable = false)
    private Sede sede;
}