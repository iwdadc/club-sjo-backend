package ar.sanjoseobrero.backend.service;

import ar.sanjoseobrero.backend.dto.InscripcionDTO;
import ar.sanjoseobrero.backend.dto.InscripcionRequestDTO;
import ar.sanjoseobrero.backend.entity.enums.EstadoInscripcion;

import java.util.List;

public interface InscripcionService {
    // Crea el alumno, tutor, datos de salud, pastorales e inscripción
    // en una sola operación - así llega del formulario público
    InscripcionDTO crearInscripcionCompleta(InscripcionRequestDTO request);

    List<InscripcionDTO> listarTodas();

    InscripcionDTO cambiarEstado(Long id, EstadoInscripcion nuevoEstado);

    List<InscripcionDTO> listarPorActividad(Long idActividad);
}
