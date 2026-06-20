// Tutor.java - Entidad del adulto responsable del alumno

package ar.sanjoseobrero.backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;


import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "tutores")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class Tutor extends Persona {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El parentesco es obligatorio")
    @Column(nullable = false, length = 30)
    private String parentesco;

    // Un tutor puede ser responsable de varios alumnos
    @OneToMany(mappedBy = "tutor", fetch = FetchType.LAZY)
    @Builder.Default
    private List<Alumno> alumnos = new ArrayList<>(); 
}
