package ar.sanjoseobrero.backend.service.impl;

import ar.sanjoseobrero.backend.dto.ProfesorDTO;
import ar.sanjoseobrero.backend.dto.ProfesorRequestDTO;
import ar.sanjoseobrero.backend.entity.Actividad;
import ar.sanjoseobrero.backend.entity.Profesor;
import ar.sanjoseobrero.backend.entity.UsuarioSistema;
import ar.sanjoseobrero.backend.entity.enums.Rol;
import ar.sanjoseobrero.backend.repository.ActividadRepository;
import ar.sanjoseobrero.backend.repository.ProfesorRepository;
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
public class ProfesorServiceImpl  implements ProfesorService {
    private final ProfesorRepository profesorRepository;
    private final UsuarioSistemaRepository usuarioSistemaRepository;
    private final ActividadRepository actividadRepository;
    private final PasswordEncoder passwordEncoder; // inyectado desde SecurityConfig

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

        // 1. Crear el usuario de login
        UsuarioSistema usuario = UsuarioSistema.builder()
            .email(request.getEmail())
            .passwordHash(passwordEncoder.encode(request.getPassword()))
            .rol(Rol.PROFESOR)
            .activo(true)
            .build();
        usuario = usuarioSistemaRepository.save(usuario);   
        
        Set<Actividad> actividades = buscarActividades(request.getIdsActividades());
        // 2. Crear el profesor con sus datos personales y actividades
        Profesor profesor = Profesor.builder()
            .nombre(request.getNombre())
            .apellido(request.getApellido())
            .dni(request.getDni())
            .usuarioSistema(usuario)
            .actividades(actividades)
            .build();

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
   
        Set<Actividad> actividades = buscarActividades(request.getIdsActividades());
        profesor.setActividades(actividades);

        // Solo actualiza email/password si vinieron en el request
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

        // Desactiva el usuario en vez de borrarlo — preserva historial de asistencias
        profesor.getUsuarioSistema().setActivo(false);
        usuarioSistemaRepository.save(profesor.getUsuarioSistema());
    }

    private Set<Actividad> buscarActividades(List<Long> idsActividades) {

    List<Actividad> encontradas = actividadRepository.findAllById(idsActividades);

    if (encontradas.size() != idsActividades.size()) {

        List<Long> idsEncontrados = encontradas.stream()
            .map(Actividad::getId)
            .toList();

        List<Long> idsFaltantes = idsActividades.stream()
            .filter(id -> !idsEncontrados.contains(id))
            .toList();

        throw new EntityNotFoundException(
            "No se encontraron las actividades con id: " + idsFaltantes
        );
    }

    return new HashSet<>(encontradas);
}
    private ProfesorDTO mapearADTO(Profesor profesor) {
        return ProfesorDTO.builder()
            .id(profesor.getId())
            .nombre(profesor.getNombre())
            .apellido(profesor.getApellido())
            .email(profesor.getUsuarioSistema().getEmail())
            .actividades(profesor.getActividades().stream().map(Actividad::getNombre).toList())
            .activo(profesor.getUsuarioSistema().getActivo())
            .build();
    }
}
