// AlumnoService.java - Interfaz del servicio de Alumno

package ar.sanjoseobrero.backend.service;

import ar.sanjoseobrero.backend.dto.AlumnoDTO;

import java.util.List;

public interface AlumnoService {
    List<AlumnoDTO> listarTodos();

    AlumnoDTO obtenerPorId(Long id);

    AlumnoDTO actualizarDatos(Long id, AlumnoDTO datosActualizados);
}
