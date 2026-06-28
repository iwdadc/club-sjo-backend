package ar.sanjoseobrero.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ActividadRequestDTO {
    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    private String descripcion;
    private String horario;

    @Positive(message = "El cupo máximo debe ser mayor a 0")
    private Integer cupoMax;

    @NotEmpty(message = "Debe asignar al menos una sede")
    private List<Long> idsSedes;
}
