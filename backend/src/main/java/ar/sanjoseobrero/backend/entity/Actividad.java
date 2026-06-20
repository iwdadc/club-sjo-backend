// Actividad.java - Entidad para las actividades/clases del club


package ar.sanjoseobrero.backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table (name = "actividades")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Actividad {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre de la actividad es obligatorio")
    @Column(nullable = false)
    private String nombre;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    private String horario;

    @Positive(message = "El cupo máximo debe ser mayor a 0")
    @Column(name = "cupo_max")
    private Integer cupoMax;

    @Builder.Default
    private Boolean activa = true;

    // RELACIONES

    // Dueña de la relación con Sede - define la tabla intermedia
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "actividad_sede",
        joinColumns = @JoinColumn(name = "id_actividad"),
        inverseJoinColumns = @JoinColumn(name = "id_sede")
    )
    @Builder.Default
    private Set<Sede> sedes = new HashSet<>();

    // Dueña de la relación con Profesor
    //@ManyToMany(fetch = FetchType.LAZY)
    //@JoinTable(
    //    name = "profesor_actividad",
    //    joinColumns = @JoinColumn(name = "id_actividad"),
    //    inverseJoinColumns = @JoinColumn(name = "id_profesor")
    //)
    //@Builder.Default
    //private Set<Profesor> profesores = new HashSet<>();

    // Inscripciones de alumnos a esta actividad
    //@OneToMany(mappedBy = "actividad", fetch = FetchType.LAZY)
    //@Builder.Default
   // private List<Inscripcion> inscripciones = new ArrayList<>();
    
}
