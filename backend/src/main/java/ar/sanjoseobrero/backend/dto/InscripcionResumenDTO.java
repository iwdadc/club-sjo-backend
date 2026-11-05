package ar.sanjoseobrero.backend.dto;

import ar.sanjoseobrero.backend.entity.enums.AnioParticipacion;
import ar.sanjoseobrero.backend.entity.enums.EstadoInscripcion;
import ar.sanjoseobrero.backend.entity.enums.RetiroMenor;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO de inscripción usado DENTRO del detalle del alumno (AlumnoDTO).
 * No reemplaza InscripcionDTO — ese lo sigue usando InscripcionServiceImpl.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InscripcionResumenDTO {

    private Long id;
    private LocalDateTime fechaInscripcion;
    private EstadoInscripcion estado;
    private AnioParticipacion anioParticipacion;
    private String whatsappContacto;
    private RetiroMenor retiroMenor;
    private String quienBusca;

    // Actividad
    private Long actividadId;
    private String actividadNombre;

    // Sede confirmada por el admin
    private Long sedeId;
    private String sedeNombre;

    // Sedes sugeridas por el alumno en el formulario
    private List<String> sedesSugeridasNombres;

    // Autorizaciones (Secciones 5 y 6)
    private Boolean autorizaActividad;
    private String firmaActividad;
    private Boolean autorizaImagen;
    private String firmaImagen;
}