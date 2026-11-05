package ar.sanjoseobrero.backend.service.impl;

import ar.sanjoseobrero.backend.dto.AlumnoDTO;
import ar.sanjoseobrero.backend.dto.DatosPastoralesDTO;
import ar.sanjoseobrero.backend.dto.DatosSaludDTO;
import ar.sanjoseobrero.backend.dto.InscripcionResumenDTO;
import ar.sanjoseobrero.backend.entity.*;
import ar.sanjoseobrero.backend.entity.enums.EstadoInscripcion;
import ar.sanjoseobrero.backend.repository.AlumnoRepository;
import ar.sanjoseobrero.backend.service.AlumnoService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AlumnoServiceImpl implements AlumnoService {

    private final AlumnoRepository alumnoRepository;

    @Override
    @Transactional(readOnly = true)
    public List<AlumnoDTO> listarTodos() {
        return alumnoRepository.findAllConRelaciones()
            .stream()
            .map(a -> mapearADTO(a, false))
            .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public AlumnoDTO obtenerPorId(Long id) {
        Alumno alumno = alumnoRepository.findByIdConDatosPrincipales(id)
            .orElseThrow(() -> new EntityNotFoundException("Alumno no encontrado con id: " + id));
        return mapearADTO(alumno, true);
    }

    @Transactional
    @Override
    public AlumnoDTO actualizarDatos(Long id, AlumnoDTO datos) {
        Alumno alumno = alumnoRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Alumno no encontrado con id: " + id));

        alumno.setNombre(datos.getNombre());
        alumno.setApellido(datos.getApellido());
        alumno.setDni(datos.getDni());
        alumno.setDomicilio(datos.getDomicilio());
        alumno.setTelefono(datos.getTelefono());
        alumno.setEmail(datos.getEmail());

        return mapearADTO(alumnoRepository.save(alumno), false);
    }
    @Override
    @Transactional(readOnly = true)
    public List<AlumnoDTO> listarConfirmados() {
    return alumnoRepository.findByInscripcionesEstado(EstadoInscripcion.CONFIRMADO)
        .stream()
        .map(a -> mapearADTO(a, false)) // false - el listado no necesita inscripciones
        .toList();
    }

    // ── MAPEO PRINCIPAL ───────────────────────────────────────────────────

    private AlumnoDTO mapearADTO(Alumno alumno, boolean conInscripciones) {
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
            .fechaRegistro(alumno.getFechaRegistro())
            .escolaridad(alumno.getEscolaridad())
            .escuela(alumno.getEscuela())
            .gradoDivision(alumno.getGradoDivision())
            .turno(alumno.getTurno())
            .ocupacion(alumno.getOcupacion())
            .convivencia(alumno.getConvivencia())
            .fotoDniFrenteUrl(alumno.getFotoDniFrenteUrl())
            .fotoDniDorsoUrl(alumno.getFotoDniDorsoUrl())
            .nombreTutor(tutor != null ? tutor.getNombre() : null)
            .apellidoTutor(tutor != null ? tutor.getApellido() : null)
            .dniTutor(tutor != null ? tutor.getDni() : null)
            .telefonoTutor(tutor != null ? tutor.getTelefono() : null)
            .parentescoTutor(tutor != null ? tutor.getParentesco() : null)
            .nombrePadre(alumno.getNombrePadre())
            .apellidoPadre(alumno.getApellidoPadre())
            .dniPadre(alumno.getDniPadre())
            .nombreMadre(alumno.getNombreMadre())
            .apellidoMadre(alumno.getApellidoMadre())
            .dniMadre(alumno.getDniMadre())
            .datosPastorales(mapearPastoral(alumno.getDatosPastorales()))
            .datosSalud(mapearSalud(alumno.getDatosSalud()))
            .inscripciones(conInscripciones ? mapearInscripciones(alumno.getInscripciones()) : null)
            .build();
    }

    // ── MAPEOS AUXILIARES ─────────────────────────────────────────────────

    private DatosPastoralesDTO mapearPastoral(DatosPastorales p) {
        if (p == null) return null;
        return DatosPastoralesDTO.builder()
            .bautismo(p.getBautismo())
            .comunion(p.getComunion())
            .confirmacion(p.getConfirmacion())
            .razonSacramento(p.getRazonSacramento())
            .gruposPastorales(p.getGruposPastorales())
            .build();
    }

    private DatosSaludDTO mapearSalud(DatosSalud s) {
        if (s == null) return null;
        return DatosSaludDTO.builder()
            .tieneObraSocial(s.getTieneObraSocial())
            .nombreObraSocial(s.getNombreObraSocial())
            .nroAfiliado(s.getNroAfiliado())
            .asma(s.getAsma())
            .diabetes(s.getDiabetes())
            .hipertension(s.getHipertension())
            .hipotension(s.getHipotension())
            .problemasCardiacos(s.getProblemasCardiacos())
            .celiaquia(s.getCeliaquia())
            .alergias(s.getAlergias())
            .detalleAlergias(s.getDetalleAlergias())
            .epilepsia(s.getEpilepsia())
            .problemasColumna(s.getProblemasColumna())
            .detalleColumna(s.getDetalleColumna())
            .problemasHuesos(s.getProblemasHuesos())
            .convulsiones(s.getConvulsiones())
            .condicionAlimentaria(s.getCondicionAlimentaria())
            .detalleAlimentaria(s.getDetalleAlimentaria())
            .desmayos(s.getDesmayos())
            .mareos(s.getMareos())
            .palpitaciones(s.getPalpitaciones())
            .dolorPecho(s.getDolorPecho())
            .mayorCansancio(s.getMayorCansancio())
            .dificultadRespirar(s.getDificultadRespirar())
            .disminucionAuditiva(s.getDisminucionAuditiva())
            .detalleAuditivo(s.getDetalleAuditivo())
            .dificultadVisual(s.getDificultadVisual())
            .detalleVisual(s.getDetalleVisual())
            .medicacion(s.getMedicacion())
            .detalleMedicacion(s.getDetalleMedicacion())
            .operacion(s.getOperacion())
            .detalleOperacion(s.getDetalleOperacion())
            .otrasObservaciones(s.getOtrasObservaciones())
            .build();
    }

    private List<InscripcionResumenDTO> mapearInscripciones(List<Inscripcion> inscripciones) {
        if (inscripciones == null) return Collections.emptyList();
        return inscripciones.stream().map(i -> {
            Actividad act = i.getActividad();
            Sede sede = i.getSede();
            return InscripcionResumenDTO.builder()
                .id(i.getId())
                .fechaInscripcion(i.getFechaInscripcion())
                .estado(i.getEstado())
                .anioParticipacion(i.getAnioParticipacion())
                .whatsappContacto(i.getWhatsappContacto())
                .retiroMenor(i.getRetiroMenor())
                .quienBusca(i.getQuienBusca())
                .actividadId(act != null ? act.getId() : null)
                .actividadNombre(act != null ? act.getNombre() : null)
                .sedeId(sede != null ? sede.getId() : null)
                .sedeNombre(sede != null ? sede.getNombre() : null)
                .sedesSugeridasNombres(
                    i.getSedesSugeridas() != null
                        ? i.getSedesSugeridas().stream().map(Sede::getNombre).toList()
                        : Collections.emptyList()
                )
                .autorizaActividad(i.getAutorizaActividad())
                .firmaActividad(i.getFirmaActividad())
                .autorizaImagen(i.getAutorizaImagen())
                .firmaImagen(i.getFirmaImagen())
                .build();
        }).toList();
    }
}