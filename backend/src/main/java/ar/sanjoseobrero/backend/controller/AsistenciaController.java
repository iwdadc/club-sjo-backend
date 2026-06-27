// AsistenciaController.java — Endpoints de asistencia
// El profesor/admin logueado se obtiene del JWT, no del body — más seguro
// Principio SRP: solo maneja entrada/salida, la lógica vive en AsistenciaService

package ar.sanjoseobrero.backend.controller;

import ar.sanjoseobrero.backend.dto.AsistenciaDTO;
import ar.sanjoseobrero.backend.dto.AsistenciaRequestDTO;
import ar.sanjoseobrero.backend.service.AsistenciaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/asistencia")
@RequiredArgsConstructor
@Tag(name = "Asistencia", description = "Registro de presente/ausente — solo ADMIN o PROFESOR asignado")
public class AsistenciaController {

    private final AsistenciaService asistenciaService;

    @PostMapping
    @Operation(summary = "Registrar asistencia masiva de una clase")
    public ResponseEntity<List<AsistenciaDTO>> registrar(
        @Valid @RequestBody AsistenciaRequestDTO request,
        Authentication authentication
    ) {
        // El email viene del JWT validado por JwtAuthFilter — nunca confiar en el body para esto
        String emailLogueado = authentication.getName();
        List<AsistenciaDTO> registradas = asistenciaService.registrarAsistenciaMasiva(request, emailLogueado);
        return ResponseEntity.ok(registradas);
    }

    @GetMapping("/actividad/{idActividad}")
    @Operation(summary = "Listar asistencia de una actividad en una fecha específica")
    public ResponseEntity<List<AsistenciaDTO>> listarPorActividadYFecha(
        @PathVariable Long idActividad,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha
    ) {
        return ResponseEntity.ok(asistenciaService.listarPorActividadYFecha(idActividad, fecha));
    }

    @GetMapping("/alumno/{idAlumno}")
    @Operation(summary = "Historial completo de asistencia de un alumno")
    public ResponseEntity<List<AsistenciaDTO>> listarPorAlumno(@PathVariable Long idAlumno) {
        return ResponseEntity.ok(asistenciaService.listarPorAlumno(idAlumno));
    }
}