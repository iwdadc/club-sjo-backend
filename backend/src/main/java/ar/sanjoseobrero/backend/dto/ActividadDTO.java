package ar.sanjoseobrero.backend.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ActividadDTO {
    private Long id;
    private String nombre;
    private String descripcion;
    private String horario;
    private Integer cupoMax;
    private Integer inscriptos; // se calcula, no se guarda
    private Boolean activa;
    private List<String> sedes;
    private List<String> profesores;
}
