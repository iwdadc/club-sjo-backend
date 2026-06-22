package ar.sanjoseobrero.backend.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import ar.sanjoseobrero.backend.dto.ProfesorDTO;
import ar.sanjoseobrero.backend.dto.ProfesorRequestDTO;
import ar.sanjoseobrero.backend.service.ProfesorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RestController
@RequestMapping
@RequiredArgsConstructor
@Tag (name = "Profesores", description = "Gestión de profesores - CRUD completo, solo ADMIN")
public class ProfesorController {
    private final ProfesorService profesorService;

    @GetMapping
    public ResponseEntity<List<ProfesorDTO>> listarTodos() {
        return ResponseEntity.ok(profesorService.listarTodos());
    }

    @PostMapping
    @Operation(summary = "Crear profesor", description = "Crea el usuario de login y los datos personales")
    public ResponseEntity<ProfesorDTO> crear(@Valid @RequestBody ProfesorRequestDTO request) {
        ProfesorDTO creado = profesorService.crear(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar profesor", description = "Password opcional — solo se cambia si se envía")
    public ResponseEntity<ProfesorDTO> actualizar(@PathVariable Long id, @Valid @RequestBody ProfesorRequestDTO request) {
        return ResponseEntity.ok(profesorService.actualizar(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar profesor", description = "Desactiva el usuario, preserva historial")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        profesorService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
