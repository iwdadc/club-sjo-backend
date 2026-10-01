package ar.sanjoseobrero.backend.dto;

import ar.sanjoseobrero.backend.entity.enums.RazonSacramento;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DatosPastoralesDTO {

    private Boolean bautismo;
    private Boolean comunion;
    private Boolean confirmacion;
    private RazonSacramento razonSacramento;
    private List<String> gruposPastorales;
}