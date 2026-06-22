package ar.sanjoseobrero.backend.service;

import ar.sanjoseobrero.backend.dto.ActividadDTO;
import ar.sanjoseobrero.backend.dto.ActividadRequestDTO;

import java.util.List;

public interface ActividadService {
    List<ActividadDTO> listarTodas();

    List<ActividadDTO> listarActivas();

    ActividadDTO obtenerPorId(Long id);

    ActividadDTO crear(ActividadRequestDTO request);

    ActividadDTO actualizar(Long id, ActividadRequestDTO request);

    void eliminar(Long id);
}
