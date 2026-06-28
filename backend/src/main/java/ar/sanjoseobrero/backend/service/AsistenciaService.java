package ar.sanjoseobrero.backend.service;

import ar.sanjoseobrero.backend.dto.AsignacionDTO;
import ar.sanjoseobrero.backend.dto.AsistenciaDTO;
import ar.sanjoseobrero.backend.dto.AsistenciaRequestDTO;

import java.time.LocalDate;
import java.util.List;

public interface AsistenciaService {

    // emailProfesorLogueado viene de la autenticación, no del body - evita falsificar al profesor
    List<AsistenciaDTO> registrarAsistenciaMasiva(AsistenciaRequestDTO request, String emailProfesorLogueado);

    List<AsistenciaDTO> listarPorActividadYFecha(Long idActividad, LocalDate fecha);

    List<AsistenciaDTO> listarPorAlumno(Long idAlumno);

    List<AsignacionDTO> listarMisAsignaciones(String emailLogueado);
}