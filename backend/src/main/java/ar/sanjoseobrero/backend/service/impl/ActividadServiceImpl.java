package ar.sanjoseobrero.backend.service.impl;

import java.util.HashSet;

import ar.sanjoseobrero.backend.dto.ActividadDTO;
import ar.sanjoseobrero.backend.dto.ActividadRequestDTO;
import ar.sanjoseobrero.backend.entity.Actividad;
import ar.sanjoseobrero.backend.entity.Asignacion;
import ar.sanjoseobrero.backend.entity.Sede;
import ar.sanjoseobrero.backend.entity.enums.EstadoInscripcion;
import ar.sanjoseobrero.backend.repository.ActividadRepository;
import ar.sanjoseobrero.backend.repository.InscripcionRepository;
import ar.sanjoseobrero.backend.repository.SedeRepository;
import ar.sanjoseobrero.backend.service.ActividadService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ActividadServiceImpl implements ActividadService {
    private final ActividadRepository actividadRepository;
    private final SedeRepository sedeRepository;
    private final InscripcionRepository inscripcionRepository;
    // CAMBIÓ: ya no necesita ProfesorRepository — idsProfesores se eliminó del request.
    // Los profesores que dan cada actividad se derivan de Asignacion, no se asignan acá.

    @Override
    @Transactional(readOnly = true)
    public List<ActividadDTO> listarTodas() {
        return actividadRepository.findAll()
            .stream()
            .map(this::mapearADTO)
            .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ActividadDTO> listarActivas() {
        return actividadRepository.findByActivaTrue()
            .stream()
            .map(this::mapearADTO)
            .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ActividadDTO obtenerPorId(Long id) {
        Actividad actividad = buscarOLanzarExcepcion(id);
        return mapearADTO(actividad);
    }

    @Override
    @Transactional
    public ActividadDTO crear(ActividadRequestDTO request) {
        Actividad actividad = Actividad.builder()
            .nombre(request.getNombre())
            .descripcion(request.getDescripcion())
            .horario(request.getHorario())
            .cupoMax(request.getCupoMax())
            .activa(true)
            .sedes(buscarSedes(request.getIdsSedes()))
            .build();

        Actividad guardada = actividadRepository.save(actividad);
        return mapearADTO(guardada);
    }

    @Override
    @Transactional
    public ActividadDTO actualizar(Long id, ActividadRequestDTO request) {
        Actividad actividad = buscarOLanzarExcepcion(id);

        actividad.setNombre(request.getNombre());
        actividad.setDescripcion(request.getDescripcion());
        actividad.setHorario(request.getHorario());
        actividad.setCupoMax(request.getCupoMax());
        actividad.setSedes(buscarSedes(request.getIdsSedes()));

        Actividad actualizada = actividadRepository.save(actividad);
        return mapearADTO(actualizada);
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        Actividad actividad = buscarOLanzarExcepcion(id);
        actividad.setActiva(false); // soft delete — no se borra, se desactiva
        actividadRepository.save(actividad);
    }

    // Métodos privados de apoyo

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

    private ActividadDTO mapearADTO(Actividad actividad) {
        int inscriptos = (int) inscripcionRepository
            .countByActividad_IdAndEstado(actividad.getId(), EstadoInscripcion.CONFIRMADO);

        // los nombres de profesores ahora se derivan de Asignacion,
        // no de una relación directa Actividad-Profesor. Se deduplica con un Set
        // por si el mismo profesor tiene esta actividad en más de una sede.
        List<String> nombresProfesores = actividad.getAsignaciones().stream()
            .map(Asignacion::getProfesor)
            .map(p -> p.getNombre() + " " + p.getApellido())
            .distinct()
            .collect(Collectors.toList());

        return ActividadDTO.builder()
            .id(actividad.getId())
            .nombre(actividad.getNombre())
            .descripcion(actividad.getDescripcion())
            .horario(actividad.getHorario())
            .cupoMax(actividad.getCupoMax())
            .inscriptos(inscriptos)
            .activa(actividad.getActiva())
            .sedes(actividad.getSedes().stream().map(Sede::getNombre).toList())
            .profesores(nombresProfesores)
            .build();
    }
}