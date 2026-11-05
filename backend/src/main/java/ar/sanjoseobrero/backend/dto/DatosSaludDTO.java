package ar.sanjoseobrero.backend.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DatosSaludDTO {

    // Obra social
    private Boolean tieneObraSocial;
    private String nombreObraSocial;
    private String nroAfiliado;

    // Condiciones de salud
    private Boolean asma;
    private Boolean diabetes;
    private Boolean hipertension;
    private Boolean hipotension;
    private Boolean problemasCardiacos;
    private Boolean celiaquia;
    private Boolean alergias;
    private String detalleAlergias;
    private Boolean epilepsia;
    private Boolean problemasColumna;
    private String detalleColumna;
    private Boolean problemasHuesos;
    private Boolean convulsiones;
    private Boolean condicionAlimentaria;
    private String detalleAlimentaria;

    // Síntomas durante el ejercicio
    private Boolean desmayos;
    private Boolean mareos;
    private Boolean palpitaciones;
    private Boolean dolorPecho;
    private Boolean mayorCansancio;
    private Boolean dificultadRespirar;

    // Otros
    private Boolean disminucionAuditiva;
    private String detalleAuditivo;
    private Boolean dificultadVisual;
    private String detalleVisual;
    private Boolean medicacion;
    private String detalleMedicacion;
    private Boolean operacion;
    private String detalleOperacion;
    private String otrasObservaciones;
}