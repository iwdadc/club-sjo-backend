package ar.sanjoseobrero.backend.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DatosSaludRequestDTO {
    @NotNull(message = "Indicar si posee obra social es obligatorio")
    private Boolean tieneObraSocial;
    private String nombreObraSocial;
    private String nroAfiliado;

    @NotNull private Boolean asma;
    @NotNull private Boolean diabetes;
    @NotNull private Boolean hipertension;
    @NotNull private Boolean hipotension;
    @NotNull private Boolean problemasCardiacos;
    @NotNull private Boolean celiaquia;

    @NotNull private Boolean alergias;
    private String detalleAlergias;

    @NotNull private Boolean epilepsia;

    @NotNull private Boolean problemasColumna;
    private String detalleColumna;

    @NotNull private Boolean problemasHuesos;
    @NotNull private Boolean convulsiones;

    @NotNull private Boolean condicionAlimentaria;
    private String detalleAlimentaria;

    @NotNull private Boolean desmayos;
    @NotNull private Boolean mareos;
    @NotNull private Boolean palpitaciones;
    @NotNull private Boolean dolorPecho;
    @NotNull private Boolean mayorCansancio;
    @NotNull private Boolean dificultadRespirar;

    @NotNull private Boolean disminucionAuditiva;
    private String detalleAuditivo;

    @NotNull private Boolean dificultadVisual;
    private String detalleVisual;

    @NotNull private Boolean medicacion;
    private String detalleMedicacion;

    @NotNull private Boolean operacion;
    private String detalleOperacion;

    private String otrasObservaciones;
}
