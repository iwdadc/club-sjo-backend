package ar.sanjoseobrero.backend.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import ar.sanjoseobrero.backend.dto.InscripcionDTO;
import ar.sanjoseobrero.backend.dto.InscripcionRequestDTO;
import ar.sanjoseobrero.backend.entity.enums.EstadoInscripcion;
import ar.sanjoseobrero.backend.service.InscripcionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/inscripciones")
@RequiredArgsConstructor
@Tag(name = "Inscripciones", description = "Gestión de inscripciones al club")
public class InscripcionController {
    private final InscripcionService inscripcionService;

    @PostMapping
    @Operation(summary = "Nueva inscripción", description = "Endpoint público del formulario de inscripción")
    public ResponseEntity<InscripcionDTO> crear(@Valid @RequestBody InscripcionRequestDTO request) {
        InscripcionDTO creada = inscripcionService.crearInscripcionCompleta(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(creada);
    }

    @GetMapping
    @Operation(summary = "Listar inscripciones", description = "Requiere autenticación")
    public ResponseEntity<List<InscripcionDTO>> listarTodas() {
        return ResponseEntity.ok(inscripcionService.listarTodas());
    }

    @GetMapping("/actividad/{idActividad}")
    @Operation(summary = "Listar inscripciones por actividad")
    public ResponseEntity<List<InscripcionDTO>> listarPorActividad(@PathVariable Long idActividad) {
        return ResponseEntity.ok(inscripcionService.listarPorActividad(idActividad));
    }

    @PatchMapping("/{id}/estado")
    @Operation(summary = "Cambiar estado de inscripción", description = "Solo ADMIN")
    public ResponseEntity<InscripcionDTO> cambiarEstado(
        @PathVariable Long id,
        @RequestBody Map<String, String> body
    ) {
        EstadoInscripcion nuevoEstado = EstadoInscripcion.valueOf(body.get("estado"));
        return ResponseEntity.ok(inscripcionService.cambiarEstado(id, nuevoEstado));
    }

    @PatchMapping("/{id}/sede")
    @Operation(summary = "Asignar sede definitiva", description = "Solo ADMIN — asigna la sede entre las sugeridas")
    public ResponseEntity<InscripcionDTO> asignarSede(
        @PathVariable Long id,
        @RequestBody Map<String, Long> body
    ) {
        return ResponseEntity.ok(inscripcionService.asignarSede(id, body.get("idSede")));
    }
}
