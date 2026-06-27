// AsistenciaRequestDTO.java - Carga masiva de asistencia para una actividad en una fecha

package ar.sanjoseobrero.backend.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AsistenciaRequestDTO {

    @NotNull(message = "La actividad es obligatoria")
    private Long idActividad;

    @NotNull(message = "La fecha es obligatoria")
    @PastOrPresent(message = "La fecha no puede ser futura")
    private LocalDate fecha;

    // El profesor NO viaja en el request — se obtiene del usuario logueado (JWT)

    @Valid
    @NotEmpty(message = "Debe registrar al menos un alumno")
    private List<DetalleAsistenciaDTO> alumnos;
}