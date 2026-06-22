// Persona.java - Entidad base para Alumno y Tutor
// @MappedSuperclass - no genera tabla propia

package ar.sanjoseobrero.backend.entity;

import ar.sanjoseobrero.backend.entity.enums.Genero;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;
import java.time.Period;
import lombok.experimental.SuperBuilder;

@MappedSuperclass
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public abstract class Persona {
    @NotBlank (message = "El nombre es obligatorio")
    @Column (nullable = false)
    private String nombre;

    @NotBlank(message = "El apellido es obligatorio")
    @Column(nullable = false)
    private String apellido;

    @NotBlank(message = "El DNI es obligatorio")
    @Column(nullable = false, unique = true, length = 20)
    private String dni;

    @NotNull(message = "La fecha de nacimiento es obligatoria")
    @Past(message = "La fecha de nacimiento debe ser en el pasado")
    @Column(name = "fecha_nacimiento", nullable = false)
    private LocalDate fechaNacimiento;

    // La edad NO se almacena — se calcula dinámicamente
    // Principio DRY: evita inconsistencias entre edad guardada y fecha real
    @Transient
    public Integer getEdad() {
        if (fechaNacimiento == null) return null;
        return Period.between(fechaNacimiento, LocalDate.now()).getYears();
    }

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private Genero genero;

    @NotBlank(message = "El domicilio es obligatorio")
    @Size(min = 5, max = 200, message = "El domicilio debe tener entre 5 y 200 caracteres")
    @Column(nullable = false)
    private String domicilio;

    @Pattern(
        regexp = "^[0-9+\\-\\s]{7,20}$",
        message = "El teléfono solo puede contener números, +, - y espacios"
    )
    @Column(length = 20)
    private String telefono;

    @Email(message = "El email no tiene un formato válido")
    @Column(length = 100)
    private String email;
}
