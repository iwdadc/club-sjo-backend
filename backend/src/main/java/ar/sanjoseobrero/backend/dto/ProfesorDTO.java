// ProfesorDTO.java - Representa un profesor hacia el exterior

package ar.sanjoseobrero.backend.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProfesorDTO {
    private Long id;
    private String nombre;
    private String apellido;
    private String email;
    private List<AsignacionDTO> asignaciones;
    private Boolean activo; 
}
