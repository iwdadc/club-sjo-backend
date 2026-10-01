package ar.sanjoseobrero.backend.service.impl;

import ar.sanjoseobrero.backend.dto.AlumnoDTO;
import ar.sanjoseobrero.backend.dto.AsignacionDTO;
import ar.sanjoseobrero.backend.dto.AsistenciaDTO;
import ar.sanjoseobrero.backend.dto.AsistenciaRequestDTO;
import ar.sanjoseobrero.backend.dto.DetalleAsistenciaDTO;
import ar.sanjoseobrero.backend.dto.HistorialAsistenciaDTO;
import ar.sanjoseobrero.backend.entity.Actividad;
import ar.sanjoseobrero.backend.entity.Alumno;
import ar.sanjoseobrero.backend.entity.Asistencia;
import ar.sanjoseobrero.backend.entity.Profesor;
import ar.sanjoseobrero.backend.entity.Sede;
import ar.sanjoseobrero.backend.entity.UsuarioSistema;
import ar.sanjoseobrero.backend.entity.enums.EstadoInscripcion;
import ar.sanjoseobrero.backend.repository.ActividadRepository;
import ar.sanjoseobrero.backend.repository.AlumnoRepository;
import ar.sanjoseobrero.backend.repository.AsistenciaRepository;
import ar.sanjoseobrero.backend.repository.InscripcionRepository;
import ar.sanjoseobrero.backend.repository.ProfesorRepository;
import ar.sanjoseobrero.backend.repository.SedeRepository;
import ar.sanjoseobrero.backend.repository.UsuarioSistemaRepository;
import ar.sanjoseobrero.backend.service.AsistenciaService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AsistenciaServiceImpl implements AsistenciaService {

    private final AsistenciaRepository asistenciaRepository;
    private final AlumnoRepository alumnoRepository;
    private final ActividadRepository actividadRepository;
    private final ProfesorRepository profesorRepository;
    private final SedeRepository sedeRepository;
    private final UsuarioSistemaRepository usuarioSistemaRepository;
    private final InscripcionRepository inscripcionRepository;

    @Override
    @Transactional
    public List<AsistenciaDTO> registrarAsistenciaMasiva(AsistenciaRequestDTO request, String emailProfesorLogueado) {

        Actividad actividad = actividadRepository.findById(request.getIdActividad())
            .orElseThrow(() -> new EntityNotFoundException("Actividad no encontrada: " + request.getIdActividad()));

        Sede sede = sedeRepository.findById(request.getIdSede())
            .orElseThrow(() -> new EntityNotFoundException("Sede no encontrada: " + request.getIdSede()));

        UsuarioSistema usuario = usuarioSistemaRepository.findByEmail(emailProfesorLogueado)
            .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado: " + emailProfesorLogueado));

        boolean esAdmin = usuario.getRol().name().equals("ADMIN");

        // Si es ADMIN puede no tener Profesor asociado — se registra sin profesor específico
        Profesor profesor = profesorRepository.findByUsuarioSistemaId(usuario.getId()).orElse(null);

        // El profesor solo puede tomar asistencia de la combinación actividad+sede
        // que tenga en sus Asignaciones (ADMIN no tiene esta restricción)
        if (!esAdmin && (profesor == null || !tieneAsignacion(profesor, actividad, sede))) {
            throw new AccessDeniedException(
                "No tenés asignada la actividad '" + actividad.getNombre()
                    + "' en la sede '" + sede.getNombre() + "'"
            );
        }

        List<AsistenciaDTO> registradas = new ArrayList<>();

        for (DetalleAsistenciaDTO detalle : request.getAlumnos()) {
            Alumno alumno = alumnoRepository.findById(detalle.getIdAlumno())
                .orElseThrow(() -> new EntityNotFoundException("Alumno no encontrado: " + detalle.getIdAlumno()));

            // Solo se puede marcar asistencia de alumnos confirmados en esa actividad
            boolean inscriptoConfirmado = inscripcionRepository
                .existsByAlumno_IdAndActividad_IdAndEstado(alumno.getId(), actividad.getId(), EstadoInscripcion.CONFIRMADO);

            if (!inscriptoConfirmado) {
                throw new IllegalStateException(
                    "El alumno " + alumno.getNombre() + " " + alumno.getApellido()
                        + " no está confirmado en esta actividad"
                );
            }

            // Evita duplicar el registro si ya se tomó asistencia ese día para ese alumno/actividad
            if (asistenciaRepository.existsByAlumnoIdAndActividadIdAndFecha(
                    alumno.getId(), actividad.getId(), request.getFecha())) {
                throw new IllegalStateException(
                    "Ya se registró la asistencia de " + alumno.getNombre() + " " + alumno.getApellido()
                        + " para el " + request.getFecha()
                );
            }

            Asistencia asistencia = Asistencia.builder()
                .fecha(request.getFecha())
                .presente(detalle.getPresente())
                .alumno(alumno)
                .actividad(actividad)
                .profesor(profesor)
                .sede(sede)
                .build();

            Asistencia guardada = asistenciaRepository.save(asistencia);
            registradas.add(mapearADTO(guardada));
        }

        return registradas;
    }

    @Override
    @Transactional(readOnly = true)
    public List<AsistenciaDTO> listarPorActividadYFecha(Long idActividad, LocalDate fecha) {
        return asistenciaRepository.findByActividadIdAndFecha(idActividad, fecha)
            .stream()
            .map(this::mapearADTO)
            .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<AsistenciaDTO> listarPorAlumno(Long idAlumno) {
        return asistenciaRepository.findByAlumnoId(idAlumno)
            .stream()
            .map(this::mapearADTO)
            .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<AsignacionDTO> listarMisAsignaciones(String emailLogueado) {
        UsuarioSistema usuario = usuarioSistemaRepository.findByEmail(emailLogueado)
            .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado: " + emailLogueado));

        Profesor profesor = profesorRepository.findByUsuarioSistemaId(usuario.getId()).orElse(null);

        // ADMIN sin Profesor asociado — no tiene asignaciones propias
        if (profesor == null) {
            return List.of();
        }

        return profesor.getAsignaciones().stream()
            .map(a -> AsignacionDTO.builder()
                .idActividad(a.getActividad().getId())
                .nombreActividad(a.getActividad().getNombre())
                .idSede(a.getSede().getId())
                .nombreSede(a.getSede().getNombre())
                .build())
            .toList();
    }
    @Override
    @Transactional(readOnly = true)
    public List<HistorialAsistenciaDTO> listarHistorialAdmin() {
        // Agrupa todas las asistencias por actividad+fecha+sede+profesor
        return asistenciaRepository.findAll().stream()
            .collect(java.util.stream.Collectors.groupingBy(
                a -> a.getActividad().getId() + "-" + a.getFecha() + "-" + a.getSede().getId()
            ))
            .values().stream()
            .map(grupo -> {
                Asistencia primera = grupo.get(0);
                int presentes = (int) grupo.stream().filter(a -> Boolean.TRUE.equals(a.getPresente())).count();
                int ausentes  = grupo.size() - presentes;

                List<HistorialAsistenciaDTO.DetalleAlumnoDTO> detalle = grupo.stream()
                    .map(a -> HistorialAsistenciaDTO.DetalleAlumnoDTO.builder()
                        .nombre(a.getAlumno().getNombre() + " " + a.getAlumno().getApellido())
                        .presente(a.getPresente())
                        .build())
                    .toList();

                return HistorialAsistenciaDTO.builder()
                    .id(primera.getId())
                    .nombreActividad(primera.getActividad().getNombre())
                    .nombreProfesor(primera.getProfesor() != null
                        ? primera.getProfesor().getNombre() + " " + primera.getProfesor().getApellido()
                        : "Administrador")
                    .nombreSede(primera.getSede().getNombre())
                    .fecha(primera.getFecha())
                    .presentes(presentes)
                    .ausentes(ausentes)
                    .total(grupo.size())
                    .detalle(detalle)
                    .build();
            })
            .sorted(java.util.Comparator.comparing(HistorialAsistenciaDTO::getFecha).reversed())
            .toList();
    }

    // Métodos privados de apoyo 

    private boolean tieneAsignacion(Profesor profesor, Actividad actividad, Sede sede) {
        return profesor.getAsignaciones().stream()
            .anyMatch(a -> a.getActividad().getId().equals(actividad.getId())
                && a.getSede().getId().equals(sede.getId()));
    }

    private AsistenciaDTO mapearADTO(Asistencia asistencia) {
        return AsistenciaDTO.builder()
            .id(asistencia.getId())
            .idAlumno(asistencia.getAlumno().getId())
            .nombreAlumno(asistencia.getAlumno().getNombre() + " " + asistencia.getAlumno().getApellido())
            .idActividad(asistencia.getActividad().getId())
            .nombreActividad(asistencia.getActividad().getNombre())
            .nombreProfesor(asistencia.getProfesor() != null
                ? asistencia.getProfesor().getNombre() + " " + asistencia.getProfesor().getApellido()
                : "Administrador")
            .idSede(asistencia.getSede().getId())
            .nombreSede(asistencia.getSede().getNombre())
            .fecha(asistencia.getFecha())
            .presente(asistencia.getPresente())
            .build();
    }
}