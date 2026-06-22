package ar.sanjoseobrero.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import ar.sanjoseobrero.backend.dto.AlumnoDTO;
import ar.sanjoseobrero.backend.entity.enums.EstadoInscripcion;
import ar.sanjoseobrero.backend.service.AlumnoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping
@RequiredArgsConstructor
@Tag (name = "Alumnos", description = "Gestión de alumnos - CRUD principal del negocio")
public class AlumnoController {
     private final AlumnoService alumnoService;

    @GetMapping
    public ResponseEntity<List<AlumnoDTO>> listarTodos() {
        return ResponseEntity.ok(alumnoService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AlumnoDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(alumnoService.obtenerPorId(id));
    }

    @GetMapping("/estado/{estado}")
    @Operation(summary = "Listar alumnos por estado")
    public ResponseEntity<List<AlumnoDTO>> listarPorEstado(@PathVariable EstadoInscripcion estado) {
        return ResponseEntity.ok(alumnoService.listarPorEstado(estado));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar datos del alumno", description = "Solo ADMIN — corrige datos mal enviados")
    public ResponseEntity<AlumnoDTO> actualizar(@PathVariable Long id, @RequestBody AlumnoDTO datos) {
        return ResponseEntity.ok(alumnoService.actualizarDatos(id, datos));
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<AlumnoDTO> cambiarEstado(@PathVariable Long id, @RequestBody Map<String, String> body) {
        EstadoInscripcion nuevoEstado = EstadoInscripcion.valueOf(body.get("estado"));
        return ResponseEntity.ok(alumnoService.actualizarEstado(id, nuevoEstado));
    }
}
