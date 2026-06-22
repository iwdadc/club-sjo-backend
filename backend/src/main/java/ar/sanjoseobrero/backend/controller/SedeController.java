package ar.sanjoseobrero.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import ar.sanjoseobrero.backend.entity.Sede;
import ar.sanjoseobrero.backend.repository.SedeRepository;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RestController
@RequestMapping
@RequiredArgsConstructor
@Tag (name = "Sedes", description = "Sedes del club")
public class SedeController {
    private final SedeRepository sedeRepository;

    @GetMapping
    public ResponseEntity<List<Sede>> listarTodas() {
        return ResponseEntity.ok(sedeRepository.findAll());
}
}
