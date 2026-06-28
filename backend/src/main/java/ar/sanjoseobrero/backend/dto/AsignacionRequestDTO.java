// AsignacionRequestDTO.java - Un par (actividad, sede) dentro del alta/edición de un profesor

package ar.sanjoseobrero.backend.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AsignacionRequestDTO {
    @NotNull(message = "La actividad es obligatoria")
    private Long idActividad;

    @NotNull(message = "La sede es obligatoria")
    private Long idSede;
}