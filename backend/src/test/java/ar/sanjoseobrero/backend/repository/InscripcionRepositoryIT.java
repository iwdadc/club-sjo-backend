package ar.sanjoseobrero.backend.repository;

import ar.sanjoseobrero.backend.entity.Actividad;
import ar.sanjoseobrero.backend.entity.Alumno;
import ar.sanjoseobrero.backend.entity.Inscripcion;
import ar.sanjoseobrero.backend.entity.Sede;
import ar.sanjoseobrero.backend.entity.Tutor;
import ar.sanjoseobrero.backend.entity.enums.EstadoInscripcion;
import ar.sanjoseobrero.backend.entity.enums.Parentesco;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Prueba de integracion con base real: Testcontainers levanta un MySQL 8 efimero en Docker.
 * Valida que las queries derivadas de Spring Data (countBy..., findBy..., existsBy...)
 * realmente filtran bien contra una base MySQL, no contra un mock.
 *
 */
@Testcontainers
@DataJpaTest(properties = "spring.jpa.hibernate.ddl-auto=create-drop")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE) // usar el MySQL del contenedor, no H2
class InscripcionRepositoryIT {

    @Container
    @ServiceConnection // Spring Boot toma url/usuario/clave del contenedor automaticamente
    static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0");

    @Autowired private InscripcionRepository inscripcionRepository;
    @Autowired private TestEntityManager em;

    private Actividad futbol;
    private Actividad ajedrez;
    private Sede norte;
    private Sede sur;
    private Alumno juan;
    private Alumno maria;
    private Alumno pedro;

    @BeforeEach
    void cargarDatos() {
        norte = em.persist(Sede.builder().nombre("Sede Norte").direccion("Calle 1").build());
        sur = em.persist(Sede.builder().nombre("Sede Sur").direccion("Calle 2").build());

        futbol = em.persist(Actividad.builder().nombre("Futbol").cupoMax(20).build());
        ajedrez = em.persist(Actividad.builder().nombre("Ajedrez").cupoMax(10).build());

        Tutor tutor = em.persist(Tutor.builder()
            .nombre("Ana").apellido("Gomez").dni("30000001").parentesco(Parentesco.MADRE).build());

        juan = em.persist(alumno("Juan", "40000001", tutor));
        maria = em.persist(alumno("Maria", "40000002", tutor));
        pedro = em.persist(alumno("Pedro", "40000003", tutor));

        // Futbol: 2 confirmadas (una en Norte, una en Sur) + 1 pendiente en Norte
        inscribir(juan, futbol, norte, EstadoInscripcion.CONFIRMADO);
        inscribir(maria, futbol, sur, EstadoInscripcion.CONFIRMADO);
        inscribir(pedro, futbol, norte, EstadoInscripcion.PENDIENTE);
        // Ajedrez: 1 confirmada de Juan
        inscribir(juan, ajedrez, norte, EstadoInscripcion.CONFIRMADO);

        em.flush();
        em.clear();
    }

    private Alumno alumno(String nombre, String dni, Tutor tutor) {
        return Alumno.builder().nombre(nombre).apellido("Test").dni(dni).tutor(tutor).build();
    }

    private void inscribir(Alumno alumno, Actividad actividad, Sede sede, EstadoInscripcion estado) {
        em.persist(Inscripcion.builder()
            .alumno(alumno).actividad(actividad).sede(sede).estado(estado)
            .whatsappContacto("1155550000")
            .build());
    }

    @Test
    @DisplayName("countByActividad_IdAndEstado cuenta solo las CONFIRMADAS de esa actividad")
    void cuentaSoloConfirmadasDeLaActividad() {
        long confirmadasFutbol = inscripcionRepository
            .countByActividad_IdAndEstado(futbol.getId(), EstadoInscripcion.CONFIRMADO);
        long pendientesFutbol = inscripcionRepository
            .countByActividad_IdAndEstado(futbol.getId(), EstadoInscripcion.PENDIENTE);
        long confirmadasAjedrez = inscripcionRepository
            .countByActividad_IdAndEstado(ajedrez.getId(), EstadoInscripcion.CONFIRMADO);

        assertThat(confirmadasFutbol).isEqualTo(2);   // no cuenta la pendiente ni las de ajedrez
        assertThat(pendientesFutbol).isEqualTo(1);
        assertThat(confirmadasAjedrez).isEqualTo(1);
    }

    @Test
    @DisplayName("findByActividad_IdAndSede_IdAndEstado filtra por actividad, sede y estado a la vez")
    void filtraPorActividadSedeYEstado() {
        List<Inscripcion> resultado = inscripcionRepository
            .findByActividad_IdAndSede_IdAndEstado(futbol.getId(), norte.getId(), EstadoInscripcion.CONFIRMADO);

        // Futbol + Norte + CONFIRMADO = solo Juan (Maria es de Sur, Pedro esta pendiente)
        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getAlumno().getId()).isEqualTo(juan.getId());
    }

    @Test
    @DisplayName("findByActividadId devuelve todas las inscripciones de la actividad, sin importar estado")
    void devuelveTodasLasDeLaActividad() {
        assertThat(inscripcionRepository.findByActividadId(futbol.getId())).hasSize(3);
        assertThat(inscripcionRepository.findByActividadId(ajedrez.getId())).hasSize(1);
    }

    @Test
    @DisplayName("existsByAlumno_IdAndActividad_IdAndEstado detecta si el alumno ya esta confirmado")
    void detectaAlumnoYaConfirmado() {
        assertThat(inscripcionRepository.existsByAlumno_IdAndActividad_IdAndEstado(
            juan.getId(), futbol.getId(), EstadoInscripcion.CONFIRMADO)).isTrue();
        // Pedro esta en futbol pero PENDIENTE, no confirmado
        assertThat(inscripcionRepository.existsByAlumno_IdAndActividad_IdAndEstado(
            pedro.getId(), futbol.getId(), EstadoInscripcion.CONFIRMADO)).isFalse();
        // Maria no esta en ajedrez
        assertThat(inscripcionRepository.existsByAlumno_IdAndActividad_IdAndEstado(
            maria.getId(), ajedrez.getId(), EstadoInscripcion.CONFIRMADO)).isFalse();
    }
}