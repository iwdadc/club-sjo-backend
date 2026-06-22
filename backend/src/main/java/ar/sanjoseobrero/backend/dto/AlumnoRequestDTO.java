package ar.sanjoseobrero.backend.dto;

import ar.sanjoseobrero.backend.entity.enums.Escolaridad;
import ar.sanjoseobrero.backend.entity.enums.Genero;
import ar.sanjoseobrero.backend.entity.enums.Turno;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AlumnoRequestDTO {
    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @NotBlank(message = "El apellido es obligatorio")
    private String apellido;

    @NotBlank(message = "El DNI es obligatorio")
    private String dni;

    @NotNull(message = "La fecha de nacimiento es obligatoria")
    @Past
    private LocalDate fechaNacimiento;

    @NotNull(message = "El género es obligatorio")
    private Genero genero;

    @NotBlank(message = "El domicilio es obligatorio")
    private String domicilio;

    private Escolaridad escolaridad;
    private String escuela;
    private String gradoDivision;
    private Turno turno;
    private String ocupacion;

    @NotBlank(message = "La convivencia es obligatoria")
    private String convivencia;

    // Datos del tutor - vienen anidados en el mismo request
    @Valid
    @NotNull(message = "Los datos del tutor son obligatorios")
    private TutorRequestDTO tutor;
}
