// AuthController.java - Endpoints de autenticación

package ar.sanjoseobrero.backend.controller;

import org.springframework.web.bind.annotation.PostMapping;

import ar.sanjoseobrero.backend.dto.LoginRequestDTO;
import ar.sanjoseobrero.backend.dto.LoginResponseDTO;
import ar.sanjoseobrero.backend.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag (name = "Autenticación", description = "Endpoints de login")
public class AuthController {
    private final AuthService authService;

    @PostMapping("/login")
    @Operation(summary = "Iniciar sesión", description = "Devuelve un JWT junto con los datos del usuario")
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO request) {
        return ResponseEntity.ok(authService.login(request));
    }
}
