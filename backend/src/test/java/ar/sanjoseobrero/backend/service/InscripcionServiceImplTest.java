package ar.sanjoseobrero.backend.service;

import ar.sanjoseobrero.backend.dto.InscripcionDTO;
import ar.sanjoseobrero.backend.entity.Actividad;
import ar.sanjoseobrero.backend.entity.Alumno;
import ar.sanjoseobrero.backend.entity.Asignacion;
import ar.sanjoseobrero.backend.entity.Inscripcion;
import ar.sanjoseobrero.backend.entity.Profesor;
import ar.sanjoseobrero.backend.entity.Sede;
import ar.sanjoseobrero.backend.entity.Tutor;
import ar.sanjoseobrero.backend.entity.UsuarioSistema;
import ar.sanjoseobrero.backend.entity.enums.EstadoInscripcion;
import ar.sanjoseobrero.backend.entity.enums.Rol;
import ar.sanjoseobrero.backend.repository.ActividadRepository;
import ar.sanjoseobrero.backend.repository.AlumnoRepository;
import ar.sanjoseobrero.backend.repository.InscripcionRepository;
import ar.sanjoseobrero.backend.repository.ProfesorRepository;
import ar.sanjoseobrero.backend.repository.SedeRepository;
import ar.sanjoseobrero.backend.repository.TutorRepository;
import ar.sanjoseobrero.backend.repository.UsuarioSistemaRepository;
import ar.sanjoseobrero.backend.service.impl.InscripcionServiceImpl;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

/**
 * Prueba UNITARIA de las reglas de negocio de InscripcionServiceImpl:
 *  - cupo maximo al confirmar
 *  - autorizacion por rol en listarPorActividad
 * Todos los repositories son mocks
 */
@ExtendWith(MockitoExtension.class)
class InscripcionServiceImplTest {

    @Mock private InscripcionRepository inscripcionRepository;
    @Mock private AlumnoRepository alumnoRepository;
    @Mock private TutorRepository tutorRepository;
    @Mock private ActividadRepository actividadRepository;
    @Mock private SedeRepository sedeRepository;
    @Mock private ProfesorRepository profesorRepository;
    @Mock private UsuarioSistemaRepository usuarioSistemaRepository;

    @InjectMocks private InscripcionServiceImpl service;

    // ---------- helpers ----------

    private Actividad actividad(Long id, Integer cupoMax) {
        return Actividad.builder().id(id).nombre("Futbol").cupoMax(cupoMax).build();
    }

    private Inscripcion inscripcion(Long id, Actividad actividad, Sede sede, EstadoInscripcion estado) {
        Tutor tutor = Tutor.builder().nombre("Ana").apellido("Gomez").telefono("1155550000").build();
        Alumno alumno = Alumno.builder()
            .id(1L).nombre("Juan").apellido("Perez").dni("40111222").tutor(tutor).build();
        return Inscripcion.builder()
            .id(id).alumno(alumno).actividad(actividad).sede(sede).estado(estado).build();
    }

    private UsuarioSistema usuario(Long id, String email, Rol rol) {
        return UsuarioSistema.builder().id(id).email(email).passwordHash("x").rol(rol).build();
    }

    // ---------- cambiarEstado: regla de cupo ----------

    @Test
    @DisplayName("rechazaCuandoNoHayCupo: no deja CONFIRMAR si la actividad ya esta llena")
    void rechazaCuandoNoHayCupo() {
        Actividad futbol = actividad(5L, 2);
        Inscripcion pendiente = inscripcion(1L, futbol, null, EstadoInscripcion.PENDIENTE);
        when(inscripcionRepository.findById(1L)).thenReturn(Optional.of(pendiente));
        when(inscripcionRepository.countByActividad_IdAndEstado(5L, EstadoInscripcion.CONFIRMADO))
            .thenReturn(2L); // cupoMax = 2 y ya hay 2 confirmadas

        assertThatThrownBy(() -> service.cambiarEstado(1L, EstadoInscripcion.CONFIRMADO))
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("No hay cupos disponibles");

        assertThat(pendiente.getEstado()).isEqualTo(EstadoInscripcion.PENDIENTE);
        verify(inscripcionRepository, never()).save(any());
    }

    @Test
    @DisplayName("confirma cuando todavia hay cupo")
    void confirmaCuandoHayCupo() {
        Actividad futbol = actividad(5L, 2);
        Inscripcion pendiente = inscripcion(1L, futbol, null, EstadoInscripcion.PENDIENTE);
        when(inscripcionRepository.findById(1L)).thenReturn(Optional.of(pendiente));
        when(inscripcionRepository.countByActividad_IdAndEstado(5L, EstadoInscripcion.CONFIRMADO))
            .thenReturn(1L); // queda 1 lugar
        when(inscripcionRepository.save(any(Inscripcion.class))).thenAnswer(inv -> inv.getArgument(0));

        InscripcionDTO dto = service.cambiarEstado(1L, EstadoInscripcion.CONFIRMADO);

        assertThat(dto.getEstado()).isEqualTo(EstadoInscripcion.CONFIRMADO);
        assertThat(pendiente.getEstado()).isEqualTo(EstadoInscripcion.CONFIRMADO);
    }

    @Test
    @DisplayName("sin cupoMax la actividad no tiene limite")
    void sinCupoMaxNoHayLimite() {
        Actividad libre = actividad(5L, null);
        Inscripcion pendiente = inscripcion(1L, libre, null, EstadoInscripcion.PENDIENTE);
        when(inscripcionRepository.findById(1L)).thenReturn(Optional.of(pendiente));
        when(inscripcionRepository.save(any(Inscripcion.class))).thenAnswer(inv -> inv.getArgument(0));

        InscripcionDTO dto = service.cambiarEstado(1L, EstadoInscripcion.CONFIRMADO);

        assertThat(dto.getEstado()).isEqualTo(EstadoInscripcion.CONFIRMADO);
    }

    @Test
    @DisplayName("el cupo solo se valida al CONFIRMAR, no al pasar a REVISION")
    void noValidaCupoParaOtrosEstados() {
        Actividad llena = actividad(5L, 1);
        Inscripcion pendiente = inscripcion(1L, llena, null, EstadoInscripcion.PENDIENTE);
        when(inscripcionRepository.findById(1L)).thenReturn(Optional.of(pendiente));
        when(inscripcionRepository.save(any(Inscripcion.class))).thenAnswer(inv -> inv.getArgument(0));

        InscripcionDTO dto = service.cambiarEstado(1L, EstadoInscripcion.REVISION);

        assertThat(dto.getEstado()).isEqualTo(EstadoInscripcion.REVISION);
        verify(inscripcionRepository, never()).countByActividad_IdAndEstado(any(), any());
    }

    @Test
    @DisplayName("cambiarEstado lanza EntityNotFoundException si la inscripcion no existe")
    void cambiarEstadoFallaSiNoExiste() {
        when(inscripcionRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.cambiarEstado(99L, EstadoInscripcion.CONFIRMADO))
            .isInstanceOf(EntityNotFoundException.class);
    }

    // ---------- listarPorActividad: autorizacion por rol ----------

    @Test
    @DisplayName("listarPorActividad: usuario inexistente -> EntityNotFoundException")
    void listarFallaSiUsuarioNoExiste() {
        when(usuarioSistemaRepository.findByEmail("nadie@club.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.listarPorActividad(5L, "nadie@club.com"))
            .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    @DisplayName("ADMIN ve todas las inscripciones de la actividad, sin filtrar")
    void adminVeTodas() {
        Actividad futbol = actividad(5L, 20);
        when(usuarioSistemaRepository.findByEmail("admin@club.com"))
            .thenReturn(Optional.of(usuario(1L, "admin@club.com", Rol.ADMIN)));
        when(inscripcionRepository.findByActividadId(5L)).thenReturn(List.of(
            inscripcion(1L, futbol, null, EstadoInscripcion.PENDIENTE),
            inscripcion(2L, futbol, null, EstadoInscripcion.CONFIRMADO)
        ));

        List<InscripcionDTO> resultado = service.listarPorActividad(5L, "admin@club.com");

        assertThat(resultado).hasSize(2);
        verifyNoInteractions(profesorRepository); // el admin no necesita perfil de profesor
    }

    @Test
    @DisplayName("PROFESOR sin perfil de profesor asociado -> AccessDeniedException")
    void profesorSinPerfilEsRechazado() {
        when(usuarioSistemaRepository.findByEmail("prof@club.com"))
            .thenReturn(Optional.of(usuario(7L, "prof@club.com", Rol.PROFESOR)));
        when(profesorRepository.findByUsuarioSistemaId(7L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.listarPorActividad(5L, "prof@club.com"))
            .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    @DisplayName("PROFESOR sin asignacion a ESA actividad -> AccessDeniedException")
    void profesorSinAsignacionEsRechazado() {
        Actividad otra = actividad(99L, 10);
        Sede sede = Sede.builder().id(3L).nombre("Sede Norte").direccion("Calle 1").build();
        Profesor profesor = Profesor.builder()
            .id(2L)
            .asignaciones(Set.of(Asignacion.builder().actividad(otra).sede(sede).build()))
            .build();

        when(usuarioSistemaRepository.findByEmail("prof@club.com"))
            .thenReturn(Optional.of(usuario(7L, "prof@club.com", Rol.PROFESOR)));
        when(profesorRepository.findByUsuarioSistemaId(7L)).thenReturn(Optional.of(profesor));

        // pide la actividad 5, pero solo tiene asignada la 99
        assertThatThrownBy(() -> service.listarPorActividad(5L, "prof@club.com"))
            .isInstanceOf(AccessDeniedException.class)
            .hasMessageContaining("5");

        verify(inscripcionRepository, never()).findByActividadId(any());
    }

    @Test
    @DisplayName("PROFESOR asignado ve SOLO las CONFIRMADAS de SU sede")
    void profesorVeSoloConfirmadasDeSuSede() {
        Actividad futbol = actividad(5L, 20);
        Sede miSede = Sede.builder().id(3L).nombre("Sede Norte").direccion("Calle 1").build();
        Profesor profesor = Profesor.builder()
            .id(2L)
            .asignaciones(Set.of(Asignacion.builder().actividad(futbol).sede(miSede).build()))
            .build();

        when(usuarioSistemaRepository.findByEmail("prof@club.com"))
            .thenReturn(Optional.of(usuario(7L, "prof@club.com", Rol.PROFESOR)));
        when(profesorRepository.findByUsuarioSistemaId(7L)).thenReturn(Optional.of(profesor));
        when(inscripcionRepository.findByActividad_IdAndSede_IdAndEstado(5L, 3L, EstadoInscripcion.CONFIRMADO))
            .thenReturn(List.of(inscripcion(1L, futbol, miSede, EstadoInscripcion.CONFIRMADO)));

        List<InscripcionDTO> resultado = service.listarPorActividad(5L, "prof@club.com");

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getEstado()).isEqualTo(EstadoInscripcion.CONFIRMADO);
        verify(inscripcionRepository, never()).findByActividadId(any()); // no usa la consulta sin filtrar
    }
}