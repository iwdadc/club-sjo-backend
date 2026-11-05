package ar.sanjoseobrero.backend.dto;

import ar.sanjoseobrero.backend.entity.enums.Parentesco;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TutorRequestDTO {
    @NotBlank(message = "El nombre del tutor es obligatorio")
    private String nombre;

    @NotBlank(message = "El apellido del tutor es obligatorio")
    private String apellido;

    @NotBlank(message = "El DNI del tutor es obligatorio")
    private String dni;

    @NotNull(message = "El parentesco es obligatorio")
    private Parentesco parentesco;

    @NotBlank(message = "El teléfono es obligatorio")
    private String telefono;

    @Email(message = "El email no es válido")
    private String email;
}
