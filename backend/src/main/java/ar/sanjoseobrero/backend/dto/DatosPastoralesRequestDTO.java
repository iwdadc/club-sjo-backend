package ar.sanjoseobrero.backend.dto;

import ar.sanjoseobrero.backend.entity.enums.RazonSacramento;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DatosPastoralesRequestDTO {
    private Boolean bautismo;
    private Boolean comunion;
    private Boolean confirmacion;

    @NotNull(message = "La razón del sacramento es obligatoria")
    private RazonSacramento razonSacramento;

    private List<String> gruposPastorales;
}
