package ar.sanjoseobrero.backend.service;

import ar.sanjoseobrero.backend.dto.InscripcionDTO;
import ar.sanjoseobrero.backend.dto.InscripcionRequestDTO;
import ar.sanjoseobrero.backend.entity.enums.EstadoInscripcion;

import java.util.List;

public interface InscripcionService {
    List<InscripcionDTO> crearInscripcionCompleta(InscripcionRequestDTO request);

    List<InscripcionDTO> listarTodas();

    InscripcionDTO cambiarEstado(Long id, EstadoInscripcion nuevoEstado);

    InscripcionDTO asignarSede(Long idInscripcion, Long idSede);
    
    List<InscripcionDTO> listarPorActividad(Long idActividad, String emailLogueado);
}
