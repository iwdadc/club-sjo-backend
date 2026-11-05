package ar.sanjoseobrero.backend.dto;

import ar.sanjoseobrero.backend.entity.enums.AnioParticipacion;
import ar.sanjoseobrero.backend.entity.enums.RetiroMenor;
import jakarta.validation.Valid;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InscripcionRequestDTO {
    @Valid
    @NotNull(message = "Los datos del alumno son obligatorios")
    private AlumnoRequestDTO alumno;

    @Valid
    @NotNull(message = "Los datos de salud son obligatorios")
    private DatosSaludRequestDTO datosSalud;

    @Valid
    @NotNull(message = "Los datos pastorales son obligatorios")
    private DatosPastoralesRequestDTO datosPastorales;

    // Sección 2 - Actividad
    @NotEmpty(message = "Debe seleccionar al menos una sede")
    private List<Long> idsSedes;

    @NotEmpty(message = "Debe seleccionar al menos una actividad")
    private List<Long> idsActividades;

    @NotNull(message = "Indicar desde cuándo participa es obligatorio")
    private AnioParticipacion anioParticipacion;

    @NotBlank(message = "El contacto de WhatsApp es obligatorio")
    private String whatsappContacto;

    @NotNull(message = "Indicar cómo se retira el menor es obligatorio")
    private RetiroMenor retiroMenor;

    private String quienBusca;

    // Sección 5 - Autorización actividad
    @AssertTrue(message = "Debe autorizar la participación en la actividad")
    private boolean autorizaActividad;

    @NotBlank(message = "La firma de autorización es obligatoria")
    private String firmaActividad;

    // Sección 6 - Autorización imagen (no obligatoria)
    private boolean autorizaImagen;
    private String firmaImagen;
}
