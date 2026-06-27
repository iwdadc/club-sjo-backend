// AsistenciaDTO.java - Representa un registro de asistencia hacia el exterior

package ar.sanjoseobrero.backend.dto;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AsistenciaDTO {
    private Long id;
    private Long idAlumno;
    private String nombreAlumno;
    private Long idActividad;
    private String nombreActividad;
    private String nombreProfesor;
    private LocalDate fecha;
    private Boolean presente;
}