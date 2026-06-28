// AsignacionDTO.java - Representa una asignación hacia el exterior (para mostrar y para editar)

package ar.sanjoseobrero.backend.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AsignacionDTO {
    private Long idActividad;
    private String nombreActividad;
    private Long idSede;
    private String nombreSede;
}