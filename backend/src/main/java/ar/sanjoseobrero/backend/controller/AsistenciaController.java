package ar.sanjoseobrero.backend.controller;

import ar.sanjoseobrero.backend.dto.AsignacionDTO;
import ar.sanjoseobrero.backend.dto.AsistenciaDTO;
import ar.sanjoseobrero.backend.dto.AsistenciaRequestDTO;
import ar.sanjoseobrero.backend.service.AsistenciaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/asistencias")
@RequiredArgsConstructor
@Tag(name = "Asistencias", description = "Registro y consulta de asistencia a las actividades")
public class AsistenciaController {

    private final AsistenciaService asistenciaService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'PROFESOR')")
    @Operation(
        summary = "Registrar asistencia de un curso",
        description = "PROFESOR (solo en sus actividades asignadas) o ADMIN. " +
            "Recibe una actividad, una fecha y la lista de alumnos con su presente/ausente."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Asistencia registrada"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos o alumno no confirmado en la actividad"),
        @ApiResponse(responseCode = "403", description = "El profesor no tiene asignada esa actividad"),
        @ApiResponse(responseCode = "404", description = "Actividad o alumno no encontrado")
    })
    public ResponseEntity<List<AsistenciaDTO>> registrar(
        @Valid @RequestBody AsistenciaRequestDTO request,
        Authentication authentication
    ) {
        // El profesor se identifica por el usuario logueado (JWT), nunca por el body
        String emailLogueado = authentication.getName();
        List<AsistenciaDTO> registradas = asistenciaService.registrarAsistenciaMasiva(request, emailLogueado);
        return ResponseEntity.status(HttpStatus.CREATED).body(registradas);
    }

    @GetMapping("/actividad/{idActividad}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROFESOR')")
    @Operation(summary = "Listar asistencia de una actividad en una fecha puntual")
    public ResponseEntity<List<AsistenciaDTO>> listarPorActividadYFecha(
        @PathVariable Long idActividad,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha
    ) {
        return ResponseEntity.ok(asistenciaService.listarPorActividadYFecha(idActividad, fecha));
    }

    @GetMapping("/alumno/{idAlumno}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Historial completo de asistencia de un alumno", description = "Solo ADMIN")
    public ResponseEntity<List<AsistenciaDTO>> listarPorAlumno(@PathVariable Long idAlumno) {
        return ResponseEntity.ok(asistenciaService.listarPorAlumno(idAlumno));
    }

    @GetMapping("/mis-asignaciones")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROFESOR')")
    @Operation(
        summary = "Mis actividades y sedes asignadas",
        description = "Devuelve las combinaciones actividad+sede del profesor logueado, " +
            "para armar el selector de sede antes de tomar asistencia. " +
            "Un ADMIN sin Profesor asociado recibe una lista vacía."
    )
    public ResponseEntity<List<AsignacionDTO>> listarMisAsignaciones(Authentication authentication) {
        String emailLogueado = authentication.getName();
        return ResponseEntity.ok(asistenciaService.listarMisAsignaciones(emailLogueado));
    }
}