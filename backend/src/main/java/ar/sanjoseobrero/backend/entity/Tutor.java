// Tutor.java - Entidad del adulto responsable del alumno

package ar.sanjoseobrero.backend.entity;

import ar.sanjoseobrero.backend.entity.enums.Parentesco;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "tutores")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Tutor extends Persona {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "El parentesco es obligatorio")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private Parentesco parentesco;

    // Un tutor puede ser responsable de varios alumnos
    @OneToMany(mappedBy = "tutor", fetch = FetchType.LAZY)
    @Builder.Default
    private List<Alumno> alumnos = new ArrayList<>(); 
}
