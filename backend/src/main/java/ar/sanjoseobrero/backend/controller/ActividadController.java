package ar.sanjoseobrero.backend.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import ar.sanjoseobrero.backend.dto.ActividadDTO;
import ar.sanjoseobrero.backend.dto.ActividadRequestDTO;
import ar.sanjoseobrero.backend.service.ActividadService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RestController
@RequestMapping ("/api/actividades")
@RequiredArgsConstructor
@Tag (name = "Actividades", description = "Gestión de actividades/clases del club")
public class ActividadController {
    private final ActividadService actividadService;

    @GetMapping
    public ResponseEntity<List<ActividadDTO>> listarTodas() {
        return ResponseEntity.ok(actividadService.listarTodas());
    }

    @GetMapping("/activas")
    @Operation(summary = "Listar solo actividades activas", description = "Público — usado por el formulario")
    public ResponseEntity<List<ActividadDTO>> listarActivas() {
        return ResponseEntity.ok(actividadService.listarActivas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ActividadDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(actividadService.obtenerPorId(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Crear actividad", description = "Solo ADMIN")
    public ResponseEntity<ActividadDTO> crear(@Valid @RequestBody ActividadRequestDTO request) {
        ActividadDTO creada = actividadService.crear(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(creada);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Actualizar actividad", description = "Solo ADMIN")
    public ResponseEntity<ActividadDTO> actualizar(@PathVariable Long id, @Valid @RequestBody ActividadRequestDTO request) {
        return ResponseEntity.ok(actividadService.actualizar(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Desactivar actividad", description = "Solo ADMIN - soft delete")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        actividadService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
