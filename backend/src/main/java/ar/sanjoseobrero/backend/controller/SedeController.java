package ar.sanjoseobrero.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import ar.sanjoseobrero.backend.dto.SedeDTO;
import ar.sanjoseobrero.backend.repository.SedeRepository;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RestController
@RequestMapping ("/api/sedes")
@RequiredArgsConstructor
@Tag (name = "Sedes", description = "Sedes del club")
public class SedeController {
    private final SedeRepository sedeRepository;

    @GetMapping
    public ResponseEntity<List<SedeDTO>> listarTodas() {
        List<SedeDTO> sedes = sedeRepository.findAll()
            .stream()
            .map(s -> SedeDTO.builder()
                .id(s.getId())
                .nombre(s.getNombre())
                .direccion(s.getDireccion())
                .build())
            .toList();
        return ResponseEntity.ok(sedes);
    }
}
