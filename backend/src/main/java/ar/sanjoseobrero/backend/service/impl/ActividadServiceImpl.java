package ar.sanjoseobrero.backend.service.impl;

import java.util.HashSet;

import ar.sanjoseobrero.backend.dto.ActividadDTO;
import ar.sanjoseobrero.backend.dto.ActividadRequestDTO;
import ar.sanjoseobrero.backend.entity.Actividad;
import ar.sanjoseobrero.backend.entity.Profesor;
import ar.sanjoseobrero.backend.entity.Sede;
import ar.sanjoseobrero.backend.entity.enums.EstadoInscripcion;
import ar.sanjoseobrero.backend.repository.ActividadRepository;
import ar.sanjoseobrero.backend.repository.InscripcionRepository;
import ar.sanjoseobrero.backend.repository.ProfesorRepository;
import ar.sanjoseobrero.backend.repository.SedeRepository;
import ar.sanjoseobrero.backend.service.ActividadService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ActividadServiceImpl implements ActividadService{
    private final ActividadRepository actividadRepository;
    private final SedeRepository sedeRepository;
    private final ProfesorRepository profesorRepository;
    private final InscripcionRepository inscripcionRepository;

    @Override
    public List<ActividadDTO> listarTodas() {
        return actividadRepository.findAll()
            .stream()
            .map(this::mapearADTO)
            .toList();
    }

    @Override
    public List<ActividadDTO> listarActivas() {
        return actividadRepository.findByActivaTrue()
            .stream()
            .map(this::mapearADTO)
            .toList();
    }

    @Override
    public ActividadDTO obtenerPorId(Long id) {
        Actividad actividad = buscarOLanzarExcepcion(id);
        return mapearADTO(actividad);
    }

    @Override
    public ActividadDTO crear(ActividadRequestDTO request) {
        Actividad actividad = Actividad.builder()
            .nombre(request.getNombre())
            .descripcion(request.getDescripcion())
            .horario(request.getHorario())
            .cupoMax(request.getCupoMax())
            .activa(true)
            .sedes(buscarSedes(request.getIdsSedes()))
            .profesores(buscarProfesores(request.getIdsProfesores()))
            .build();

        Actividad guardada = actividadRepository.save(actividad);
        return mapearADTO(guardada);
    }

    @Override
    public ActividadDTO actualizar(Long id, ActividadRequestDTO request) {
        Actividad actividad = buscarOLanzarExcepcion(id);

        actividad.setNombre(request.getNombre());
        actividad.setDescripcion(request.getDescripcion());
        actividad.setHorario(request.getHorario());
        actividad.setCupoMax(request.getCupoMax());
        actividad.setSedes(buscarSedes(request.getIdsSedes()));
        actividad.setProfesores(buscarProfesores(request.getIdsProfesores()));

        Actividad actualizada = actividadRepository.save(actividad);
        return mapearADTO(actualizada);
    }

    @Override
    public void eliminar(Long id) {
        Actividad actividad = buscarOLanzarExcepcion(id);
        actividad.setActiva(false); // soft delete — no se borra, se desactiva
        actividadRepository.save(actividad);
    }

    // ── Métodos privados de apoyo ──

    private Actividad buscarOLanzarExcepcion(Long id) {
        return actividadRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Actividad no encontrada: " + id));
    }

    private Set<Sede> buscarSedes(List<Long> idsSedes) {
    if (idsSedes == null || idsSedes.isEmpty()) return new HashSet<>();

    List<Sede> sedesEncontradas = sedeRepository.findAllById(idsSedes);

    if (sedesEncontradas.size() != idsSedes.size()) {
        List<Long> idsEncontrados = sedesEncontradas.stream().map(Sede::getId).toList();
        List<Long> idsFaltantes = idsSedes.stream()
            .filter(id -> !idsEncontrados.contains(id))
            .toList();
        throw new EntityNotFoundException("No se encontraron las sedes con id: " + idsFaltantes);
    }

    return new HashSet<>(sedesEncontradas);
    }   

    private Set<Profesor> buscarProfesores(List<Long> idsProfesores) {
    if (idsProfesores == null || idsProfesores.isEmpty()) return new HashSet<>();

    List<Profesor> profesoresEncontrados = profesorRepository.findAllById(idsProfesores);

    if (profesoresEncontrados.size() != idsProfesores.size()) {
        List<Long> idsEncontrados = profesoresEncontrados.stream().map(Profesor::getId).toList();
        List<Long> idsFaltantes = idsProfesores.stream()
            .filter(id -> !idsEncontrados.contains(id))
            .toList();
        throw new EntityNotFoundException("No se encontraron los profesores con id: " + idsFaltantes);
    }

    return new HashSet<>(profesoresEncontrados);
    }

    private ActividadDTO mapearADTO(Actividad actividad) {
        int inscriptos = (int) inscripcionRepository
        .countByActividad_IdAndEstado(actividad.getId(), EstadoInscripcion.CONFIRMADO);
            
        return ActividadDTO.builder()
            .id(actividad.getId())
            .nombre(actividad.getNombre())
            .descripcion(actividad.getDescripcion())
            .horario(actividad.getHorario())
            .cupoMax(actividad.getCupoMax())
            .inscriptos(inscriptos)
            .activa(actividad.getActiva())
            .sedes(actividad.getSedes().stream().map(Sede::getNombre).toList())
            .profesores(actividad.getProfesores().stream()
                .map(p -> p.getNombre() + " " + p.getApellido())
                .toList())
            .build();
    }
}
