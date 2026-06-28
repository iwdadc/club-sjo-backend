// ProfesorRequestDTO.java - Datos para crear o editar un profesor (admin)

package ar.sanjoseobrero.backend.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProfesorRequestDTO {
    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @NotBlank(message = "El apellido es obligatorio")
    private String apellido;

    @NotBlank(message = "El DNI es obligatorio")
    private String dni;

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El email no es válido")
    private String email;

    // Obligatoria al crear, opcional al editar (se valida en el service)
    private String password;

    // Cada elemento indica también la sede, ya que un profesor puede dar la misma actividad en sedes distintas
    @Valid
    @NotEmpty(message = "Debe asignar al menos una actividad con su sede")
    private List<AsignacionRequestDTO> asignaciones;
}
