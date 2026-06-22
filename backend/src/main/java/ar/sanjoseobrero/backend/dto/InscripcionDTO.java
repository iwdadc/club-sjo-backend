package ar.sanjoseobrero.backend.dto;

import ar.sanjoseobrero.backend.entity.enums.EstadoInscripcion;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InscripcionDTO {
    private Long id;
    private String nombreAlumno;
    private String dniAlumno;
    private List<String> actividades;
    private List<String> sedes;
    private String nombreTutor;
    private String telefonoTutor;
    private LocalDateTime fechaInscripcion;
    private EstadoInscripcion estado;
}
