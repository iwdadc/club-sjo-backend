package ar.sanjoseobrero.backend.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import ar.sanjoseobrero.backend.entity.enums.Genero;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class AlumnoDTO {
    private Long id;
    private String nombre;
    private String apellido;
    private String dni;
    private LocalDate fechaNacimiento;
    private Integer edad; // se calcula en la entity
    private Genero genero;
    private String domicilio;
    private String telefono;
    private String email;

    private String escuela;
    private String gradoDivision;
    private String ocupacion;

    private LocalDateTime fechaRegistro;

    // Solo los datos básicos del tutor — no toda la entidad
    private String nombreTutor;
    private String apellidoTutor;
    private String telefonoTutor;
}
