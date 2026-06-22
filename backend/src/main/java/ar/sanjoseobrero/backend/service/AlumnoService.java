// AlumnoService.java - Interfaz del servicio de Alumno

package ar.sanjoseobrero.backend.service;

import ar.sanjoseobrero.backend.dto.AlumnoDTO;
import ar.sanjoseobrero.backend.entity.enums.EstadoInscripcion;

import java.util.List;

public interface AlumnoService {
    List<AlumnoDTO> listarTodos();

    AlumnoDTO obtenerPorId(Long id);

    List<AlumnoDTO> listarPorEstado(EstadoInscripcion estado);

    AlumnoDTO actualizarEstado(Long id, EstadoInscripcion nuevoEstado);

    AlumnoDTO actualizarDatos(Long id, AlumnoDTO datosActualizados);
}
