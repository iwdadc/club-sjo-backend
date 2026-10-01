package ar.sanjoseobrero.backend.service;

import ar.sanjoseobrero.backend.dto.ActividadDTO;
import ar.sanjoseobrero.backend.dto.ActividadRequestDTO;
import ar.sanjoseobrero.backend.entity.Actividad;
import ar.sanjoseobrero.backend.entity.Sede;
import ar.sanjoseobrero.backend.entity.enums.EstadoInscripcion;
import ar.sanjoseobrero.backend.repository.ActividadRepository;
import ar.sanjoseobrero.backend.repository.InscripcionRepository;
import ar.sanjoseobrero.backend.repository.SedeRepository;
import ar.sanjoseobrero.backend.service.impl.ActividadServiceImpl;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Prueba UNITARIA: ActividadServiceImpl se prueba aislado.
 * Los 3 repositories son mocks, no hay Spring ni base de datos.
 */
@ExtendWith(MockitoExtension.class)
class ActividadServiceImplTest {

    @Mock private ActividadRepository actividadRepository;
    @Mock private SedeRepository sedeRepository;
    @Mock private InscripcionRepository inscripcionRepository;

    @InjectMocks private ActividadServiceImpl service;

    // ---------- eliminar (soft delete) ----------

    @Test
    @DisplayName("eliminar() desactiva la actividad (activa=false) y NO la borra")
    void eliminarHaceSoftDelete() {
        Actividad actividad = Actividad.builder().id(1L).nombre("Futbol").activa(true).build();
        when(actividadRepository.findById(1L)).thenReturn(Optional.of(actividad));

        service.eliminar(1L);

        assertThat(actividad.getActiva()).isFalse();
        verify(actividadRepository).save(actividad);
        verify(actividadRepository, never()).delete(any());
        verify(actividadRepository, never()).deleteById(any());
    }

    @Test
    @DisplayName("eliminar() lanza EntityNotFoundException si la actividad no existe")
    void eliminarFallaSiNoExiste() {
        when(actividadRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.eliminar(99L))
            .isInstanceOf(EntityNotFoundException.class)
            .hasMessageContaining("99");

        verify(actividadRepository, never()).save(any());
    }

    // ---------- crear ----------

    @Test
    @DisplayName("crear() falla si se pide una sede que no existe y no guarda nada")
    void crearFallaConSedeInexistente() {
        ActividadRequestDTO request = ActividadRequestDTO.builder()
            .nombre("Futbol")
            .cupoMax(20)
            .idsSedes(List.of(99L))
            .build();
        when(sedeRepository.findAllById(List.of(99L))).thenReturn(List.of());

        assertThatThrownBy(() -> service.crear(request))
            .isInstanceOf(EntityNotFoundException.class)
            .hasMessageContaining("99");

        verify(actividadRepository, never()).save(any());
    }

    @Test
    @DisplayName("crear() guarda la actividad como activa, con sus sedes, y devuelve el DTO")
    void crearGuardaActividadActiva() {
        Sede sede = Sede.builder().id(1L).nombre("Sede Centro").direccion("Calle 123").build();
        ActividadRequestDTO request = ActividadRequestDTO.builder()
            .nombre("Futbol")
            .descripcion("Entrenamiento")
            .horario("Lunes 18hs")
            .cupoMax(20)
            .idsSedes(List.of(1L))
            .build();

        when(sedeRepository.findAllById(List.of(1L))).thenReturn(List.of(sede));
        // el save "real" asigna un id; lo simulamos devolviendo la misma entidad con id
        when(actividadRepository.save(any(Actividad.class))).thenAnswer(inv -> {
            Actividad a = inv.getArgument(0);
            a.setId(10L);
            return a;
        });
        when(inscripcionRepository.countByActividad_IdAndEstado(10L, EstadoInscripcion.CONFIRMADO))
            .thenReturn(3L);

        ActividadDTO dto = service.crear(request);

        ArgumentCaptor<Actividad> captor = ArgumentCaptor.forClass(Actividad.class);
        verify(actividadRepository).save(captor.capture());
        assertThat(captor.getValue().getActiva()).isTrue();
        assertThat(captor.getValue().getSedes()).containsExactly(sede);

        assertThat(dto.getId()).isEqualTo(10L);
        assertThat(dto.getNombre()).isEqualTo("Futbol");
        assertThat(dto.getActiva()).isTrue();
        assertThat(dto.getInscriptos()).isEqualTo(3);
        assertThat(dto.getSedes()).containsExactly("Sede Centro");
    }
}