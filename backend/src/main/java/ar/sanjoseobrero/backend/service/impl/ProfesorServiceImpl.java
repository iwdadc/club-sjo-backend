package ar.sanjoseobrero.backend.service.impl;

import ar.sanjoseobrero.backend.dto.AsignacionDTO;
import ar.sanjoseobrero.backend.dto.AsignacionRequestDTO;
import ar.sanjoseobrero.backend.dto.ProfesorDTO;
import ar.sanjoseobrero.backend.dto.ProfesorRequestDTO;
import ar.sanjoseobrero.backend.entity.Actividad;
import ar.sanjoseobrero.backend.entity.Asignacion;
import ar.sanjoseobrero.backend.entity.Profesor;
import ar.sanjoseobrero.backend.entity.Sede;
import ar.sanjoseobrero.backend.entity.UsuarioSistema;
import ar.sanjoseobrero.backend.entity.enums.Rol;
import ar.sanjoseobrero.backend.repository.ActividadRepository;
import ar.sanjoseobrero.backend.repository.ProfesorRepository;
import ar.sanjoseobrero.backend.repository.SedeRepository;
import ar.sanjoseobrero.backend.repository.UsuarioSistemaRepository;
import ar.sanjoseobrero.backend.service.ProfesorService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ProfesorServiceImpl implements ProfesorService {
    private final ProfesorRepository profesorRepository;
    private final UsuarioSistemaRepository usuarioSistemaRepository;
    private final ActividadRepository actividadRepository;
    private final SedeRepository sedeRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional(readOnly = true)
    public List<ProfesorDTO> listarTodos() {
        return profesorRepository.findAll()
            .stream()
            .map(this::mapearADTO)
            .toList();
    }

    @Override
    @Transactional
    public ProfesorDTO crear(ProfesorRequestDTO request) {
        if (usuarioSistemaRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Ya existe un usuario con ese email");
        }

        UsuarioSistema usuario = UsuarioSistema.builder()
            .email(request.getEmail())
            .passwordHash(passwordEncoder.encode(request.getPassword()))
            .rol(Rol.PROFESOR)
            .activo(true)
            .build();
        usuario = usuarioSistemaRepository.save(usuario);

        Profesor profesor = Profesor.builder()
            .nombre(request.getNombre())
            .apellido(request.getApellido())
            .dni(request.getDni())
            .usuarioSistema(usuario)
            .build();

        profesor.setAsignaciones(construirAsignaciones(profesor, request.getAsignaciones()));

        Profesor guardado = profesorRepository.save(profesor);
        return mapearADTO(guardado);
    }

    @Override
    @Transactional
    public ProfesorDTO actualizar(Long id, ProfesorRequestDTO request) {
        Profesor profesor = profesorRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Profesor no encontrado: " + id));

        profesor.setNombre(request.getNombre());
        profesor.setApellido(request.getApellido());
        profesor.setDni(request.getDni());

        // Reemplaza TODAS las asignaciones — gracias a orphanRemoval en la entity,
        // las que ya no estén en el nuevo Set se borran solas de la base.
        profesor.getAsignaciones().clear();
        profesor.getAsignaciones().addAll(construirAsignaciones(profesor, request.getAsignaciones()));

        UsuarioSistema usuario = profesor.getUsuarioSistema();
        if (request.getEmail() != null && !request.getEmail().isBlank()) {
            usuario.setEmail(request.getEmail());
        }
        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            usuario.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        }

        Profesor actualizado = profesorRepository.save(profesor);
        return mapearADTO(actualizado);
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        Profesor profesor = profesorRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Profesor no encontrado: " + id));

        profesor.getUsuarioSistema().setActivo(false);
        usuarioSistemaRepository.save(profesor.getUsuarioSistema());
    }

    // ── Métodos privados de apoyo ──

    // Construye el Set<Asignacion> a partir del request, validando que cada
    // sede elegida sea efectivamente una de las sedes donde se dicta esa actividad.
    private Set<Asignacion> construirAsignaciones(Profesor profesor, List<AsignacionRequestDTO> request) {
        Set<Asignacion> asignaciones = new HashSet<>();

        for (AsignacionRequestDTO item : request) {
            Actividad actividad = actividadRepository.findById(item.getIdActividad())
                .orElseThrow(() -> new EntityNotFoundException("Actividad no encontrada: " + item.getIdActividad()));

            Sede sede = sedeRepository.findById(item.getIdSede())
                .orElseThrow(() -> new EntityNotFoundException("Sede no encontrada: " + item.getIdSede()));

            boolean sedeValida = actividad.getSedes().stream()
                .anyMatch(s -> s.getId().equals(sede.getId()));

            if (!sedeValida) {
                throw new IllegalStateException(
                    "La actividad '" + actividad.getNombre() + "' no se dicta en la sede '" + sede.getNombre() + "'"
                );
            }

            asignaciones.add(Asignacion.builder()
                .profesor(profesor)
                .actividad(actividad)
                .sede(sede)
                .build());
        }

        return asignaciones;
    }

    private ProfesorDTO mapearADTO(Profesor profesor) {
        List<AsignacionDTO> asignaciones = profesor.getAsignaciones().stream()
            .map(a -> AsignacionDTO.builder()
                .idActividad(a.getActividad().getId())
                .nombreActividad(a.getActividad().getNombre())
                .idSede(a.getSede().getId())
                .nombreSede(a.getSede().getNombre())
                .build())
            .toList();

        return ProfesorDTO.builder()
            .id(profesor.getId())
            .nombre(profesor.getNombre())
            .apellido(profesor.getApellido())
            .email(profesor.getUsuarioSistema().getEmail())
            .asignaciones(asignaciones)
            .activo(profesor.getUsuarioSistema().getActivo())
            .build();
    }
}