package ar.sanjoseobrero.backend.dto;

import lombok.*;
import java.time.LocalDate;
import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class HistorialAsistenciaDTO {
    private Long id;
    private String nombreActividad;
    private String nombreProfesor;
    private String nombreSede;
    private LocalDate fecha;
    private int presentes;
    private int ausentes;
    private int total;
    private List<DetalleAlumnoDTO> detalle;

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class DetalleAlumnoDTO {
        private String nombre;
        private Boolean presente;
    }
}