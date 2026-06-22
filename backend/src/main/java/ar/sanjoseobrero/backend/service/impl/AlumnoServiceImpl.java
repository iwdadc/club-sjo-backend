package ar.sanjoseobrero.backend.service.impl;

import ar.sanjoseobrero.backend.dto.AlumnoDTO;
import ar.sanjoseobrero.backend.entity.Alumno;
import ar.sanjoseobrero.backend.entity.Tutor;
import ar.sanjoseobrero.backend.entity.enums.EstadoInscripcion;
import ar.sanjoseobrero.backend.repository.AlumnoRepository;
import ar.sanjoseobrero.backend.service.AlumnoService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AlumnoServiceImpl implements AlumnoService {
    private final AlumnoRepository alumnoRepository;

    @Override
    public List<AlumnoDTO> listarTodos() {
        return alumnoRepository.findAll()
            .stream()
            .map(this::mapearADTO)
            .toList();
    }

    @Override
    public AlumnoDTO obtenerPorId(Long id) {
        Alumno alumno = buscarAlumnoOLanzarExcepcion(id);
        return mapearADTO(alumno);
    }

    @Override
    public List<AlumnoDTO> listarPorEstado(EstadoInscripcion estado) {
        return alumnoRepository.findByEstado(estado)
            .stream()
            .map(this::mapearADTO)
            .toList();
    }

    @Override
    public AlumnoDTO actualizarEstado(Long id, EstadoInscripcion nuevoEstado) {
        Alumno alumno = buscarAlumnoOLanzarExcepcion(id);
        alumno.setEstado(nuevoEstado);
        Alumno actualizado = alumnoRepository.save(alumno);
        return mapearADTO(actualizado);
    }

    @Override
    public AlumnoDTO actualizarDatos(Long id, AlumnoDTO datosActualizados) {
        Alumno alumno = buscarAlumnoOLanzarExcepcion(id);

        // Solo actualiza los campos que el admin puede corregir
        // (ej: si el alumno quedó en REVISION por un dato mal escrito)
        alumno.setNombre(datosActualizados.getNombre());
        alumno.setApellido(datosActualizados.getApellido());
        alumno.setDni(datosActualizados.getDni());
        alumno.setDomicilio(datosActualizados.getDomicilio());
        alumno.setTelefono(datosActualizados.getTelefono());
        alumno.setEmail(datosActualizados.getEmail());

        Alumno actualizado = alumnoRepository.save(alumno);
        return mapearADTO(actualizado);
    }

    // Métodos privados de apoyo

    private Alumno buscarAlumnoOLanzarExcepcion(Long id) {
        return alumnoRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Alumno no encontrado con id: " + id));
    }

    // Mapeo Entity -> DTO
    // este método solo transforma datos, no decide lógica de negocio
    private AlumnoDTO mapearADTO(Alumno alumno) {
        Tutor tutor = alumno.getTutor();

        return AlumnoDTO.builder()
        .id(alumno.getId())
        .nombre(alumno.getNombre())
        .apellido(alumno.getApellido())
        .dni(alumno.getDni())
        .fechaNacimiento(alumno.getFechaNacimiento())
        .edad(alumno.getEdad())
        .genero(alumno.getGenero())
        .domicilio(alumno.getDomicilio())
        .telefono(alumno.getTelefono())
        .email(alumno.getEmail())
        .escuela(alumno.getEscuela())
        .gradoDivision(alumno.getGradoDivision())
        .ocupacion(alumno.getOcupacion())
        .estado(alumno.getEstado())
        .fechaRegistro(alumno.getFechaRegistro())
        .nombreTutor(tutor != null ? tutor.getNombre() : null)
        .apellidoTutor(tutor != null ? tutor.getApellido() : null)
        .telefonoTutor(tutor != null ? tutor.getTelefono() : null)
        .build();
    }   
}
