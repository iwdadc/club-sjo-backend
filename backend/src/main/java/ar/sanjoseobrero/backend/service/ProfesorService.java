package ar.sanjoseobrero.backend.service;

import ar.sanjoseobrero.backend.dto.ProfesorDTO;
import ar.sanjoseobrero.backend.dto.ProfesorRequestDTO;

import java.util.List;
public interface ProfesorService {
    List<ProfesorDTO> listarTodos();

    ProfesorDTO crear(ProfesorRequestDTO request);

    ProfesorDTO actualizar(Long id, ProfesorRequestDTO request);

    void eliminar(Long id);
}
