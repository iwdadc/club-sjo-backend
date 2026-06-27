// DetalleAsistenciaDTO.java - Un alumno dentro de la lista de asistencia del curso

package ar.sanjoseobrero.backend.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DetalleAsistenciaDTO {

    @NotNull(message = "El alumno es obligatorio")
    private Long idAlumno;

    @NotNull(message = "Debe indicar si estuvo presente")
    private Boolean presente;
}