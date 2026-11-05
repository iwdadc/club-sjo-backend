// LoginResponseDTO.java - Respuesta del login con el token y datos del usuario

package ar.sanjoseobrero.backend.dto;

import ar.sanjoseobrero.backend.entity.enums.Rol;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginResponseDTO {
    private String token;
    private String email;
    private String nombre;
    private Rol rol;
}
