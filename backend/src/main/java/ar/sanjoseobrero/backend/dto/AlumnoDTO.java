package ar.sanjoseobrero.backend.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import ar.sanjoseobrero.backend.entity.enums.Escolaridad;
import ar.sanjoseobrero.backend.entity.enums.Genero;
import ar.sanjoseobrero.backend.entity.enums.Parentesco;
import ar.sanjoseobrero.backend.entity.enums.Turno;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AlumnoDTO {

    // ── DATOS BÁSICOS (Sección 1) ──────────────────────────────────────────
    private Long id;
    private String nombre;
    private String apellido;
    private String dni;
    private LocalDate fechaNacimiento;
    private Integer edad;
    private Genero genero;
    private String domicilio;
    private String telefono;
    private String email;
    private LocalDateTime fechaRegistro;

    // ── ESCOLARIDAD (Sección 1) ────────────────────────────────────────────
    private Escolaridad escolaridad;
    private String escuela;
    private String gradoDivision;
    private Turno turno;
    private String ocupacion;

    // ── CONVIVENCIA (Sección 1) ────────────────────────────────────────────
    private String convivencia;

    // ── FOTOS DNI — URLs de Cloudinary (pendiente) ────────────────────────
    private String fotoDniFrenteUrl;
    private String fotoDniDorsoUrl;

    // ── ADULTO RESPONSABLE / TUTOR (Sección 1) ────────────────────────────
    private String nombreTutor;
    private String apellidoTutor;
    private String dniTutor;
    private String telefonoTutor;
    private Parentesco parentescoTutor;

    // ── PADRE Y MADRE (Sección 1, opcional) ───────────────────────────────
    private String nombrePadre;
    private String apellidoPadre;
    private String dniPadre;
    private String nombreMadre;
    private String apellidoMadre;
    private String dniMadre;

    // ── PASTORAL (Sección 3) ───────────────────────────────────────────────
    private DatosPastoralesDTO datosPastorales;

    // ── SALUD (Sección 4) ─────────────────────────────────────────────────
    private DatosSaludDTO datosSalud;

    // ── INSCRIPCIONES (Secciones 2, 5 y 6) ───────────────────────────────
    private List<InscripcionResumenDTO> inscripciones;
}