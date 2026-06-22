package ar.sanjoseobrero.backend.service.impl;

import ar.sanjoseobrero.backend.service.InscripcionService;
import ar.sanjoseobrero.backend.dto.DatosPastoralesRequestDTO;
import ar.sanjoseobrero.backend.dto.DatosSaludRequestDTO;
import ar.sanjoseobrero.backend.dto.InscripcionDTO;
import ar.sanjoseobrero.backend.dto.InscripcionRequestDTO;
import ar.sanjoseobrero.backend.entity.Actividad;
import ar.sanjoseobrero.backend.entity.Alumno;
import ar.sanjoseobrero.backend.entity.DatosPastorales;
import ar.sanjoseobrero.backend.entity.DatosSalud;
import ar.sanjoseobrero.backend.entity.Inscripcion;
import ar.sanjoseobrero.backend.entity.Sede;
import ar.sanjoseobrero.backend.entity.Tutor;
import ar.sanjoseobrero.backend.entity.enums.EstadoInscripcion;
import ar.sanjoseobrero.backend.repository.ActividadRepository;
import ar.sanjoseobrero.backend.repository.AlumnoRepository;
import ar.sanjoseobrero.backend.repository.InscripcionRepository;
import ar.sanjoseobrero.backend.repository.SedeRepository;
import ar.sanjoseobrero.backend.repository.TutorRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;

import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class InscripcionServiceImpl implements InscripcionService {
    private final InscripcionRepository inscripcionRepository;
    private final AlumnoRepository alumnoRepository;
    private final TutorRepository tutorRepository;
    private final ActividadRepository actividadRepository;
    private final SedeRepository sedeRepository;

    @Override
    @Transactional // Si algo falla a mitad de camino, se revierte todo (rollback)
    public InscripcionDTO crearInscripcionCompleta(InscripcionRequestDTO request) {

        // 1. Crear el Tutor
        Tutor tutor = Tutor.builder()
            .nombre(request.getAlumno().getTutor().getNombre())
            .apellido(request.getAlumno().getTutor().getApellido())
            .dni(request.getAlumno().getTutor().getDni())
            .parentesco(request.getAlumno().getTutor().getParentesco())
            .telefono(request.getAlumno().getTutor().getTelefono())
            .email(request.getAlumno().getTutor().getEmail())
            .build();
        tutor = tutorRepository.save(tutor);

        // 2. Crear el Alumno asociado al Tutor
        Alumno alumno = Alumno.builder()
            .nombre(request.getAlumno().getNombre())
            .apellido(request.getAlumno().getApellido())
            .dni(request.getAlumno().getDni())
            .fechaNacimiento(request.getAlumno().getFechaNacimiento())
            .genero(request.getAlumno().getGenero())
            .domicilio(request.getAlumno().getDomicilio())
            .escolaridad(request.getAlumno().getEscolaridad())
            .escuela(request.getAlumno().getEscuela())
            .gradoDivision(request.getAlumno().getGradoDivision())
            .turno(request.getAlumno().getTurno())
            .ocupacion(request.getAlumno().getOcupacion())
            .convivencia(request.getAlumno().getConvivencia())
            .estado(EstadoInscripcion.PENDIENTE) // siempre arranca pendiente
            .tutor(tutor)
            .build();
        alumno = alumnoRepository.save(alumno);

        // 3. Crear DatosSalud asociado al Alumno
        DatosSaludRequestDTO saludReq = request.getDatosSalud();
        DatosSalud datosSalud = DatosSalud.builder()
            .tieneObraSocial(saludReq.getTieneObraSocial())
            .nombreObraSocial(saludReq.getNombreObraSocial())
            .nroAfiliado(saludReq.getNroAfiliado())
            .asma(saludReq.getAsma())
            .diabetes(saludReq.getDiabetes())
            .hipertension(saludReq.getHipertension())
            .hipotension(saludReq.getHipotension())
            .problemasCardiacos(saludReq.getProblemasCardiacos())
            .celiaquia(saludReq.getCeliaquia())
            .alergias(saludReq.getAlergias())
            .detalleAlergias(saludReq.getDetalleAlergias())
            .epilepsia(saludReq.getEpilepsia())
            .problemasColumna(saludReq.getProblemasColumna())
            .detalleColumna(saludReq.getDetalleColumna())
            .problemasHuesos(saludReq.getProblemasHuesos())
            .convulsiones(saludReq.getConvulsiones())
            .condicionAlimentaria(saludReq.getCondicionAlimentaria())
            .detalleAlimentaria(saludReq.getDetalleAlimentaria())
            .desmayos(saludReq.getDesmayos())
            .mareos(saludReq.getMareos())
            .palpitaciones(saludReq.getPalpitaciones())
            .dolorPecho(saludReq.getDolorPecho())
            .mayorCansancio(saludReq.getMayorCansancio())
            .dificultadRespirar(saludReq.getDificultadRespirar())
            .disminucionAuditiva(saludReq.getDisminucionAuditiva())
            .detalleAuditivo(saludReq.getDetalleAuditivo())
            .dificultadVisual(saludReq.getDificultadVisual())
            .detalleVisual(saludReq.getDetalleVisual())
            .medicacion(saludReq.getMedicacion())
            .detalleMedicacion(saludReq.getDetalleMedicacion())
            .operacion(saludReq.getOperacion())
            .detalleOperacion(saludReq.getDetalleOperacion())
            .otrasObservaciones(saludReq.getOtrasObservaciones())
            .alumno(alumno)
            .build();
        alumno.setDatosSalud(datosSalud); // sincroniza la relación bidireccional

        // 4. Crear DatosPastorales asociado al Alumno
        DatosPastoralesRequestDTO pastoralReq = request.getDatosPastorales();
        DatosPastorales datosPastorales = DatosPastorales.builder()
            .bautismo(pastoralReq.getBautismo())
            .comunion(pastoralReq.getComunion())
            .confirmacion(pastoralReq.getConfirmacion())
            .razonSacramento(pastoralReq.getRazonSacramento())
            .gruposPastorales(pastoralReq.getGruposPastorales())
            .alumno(alumno)
            .build();
        alumno.setDatosPastorales(datosPastorales);

        alumnoRepository.save(alumno); // cascada: guarda salud y pastorales también

        // 5. Buscar las actividades y sedes elegidas
        List<Actividad> actividades = request.getIdsActividades().stream()
            .map(id -> actividadRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Actividad no encontrada: " + id)))
            .toList();

        // Todas las sedes sugeridas por el alumno - el admin asigna la definitiva después
        Set<Sede> sedesSugeridas = buscarSedes(request.getIdsSedes());

        // 6. Crear una Inscripcion por cada actividad elegida
        Inscripcion ultimaInscripcion = null;
        for (Actividad actividad : actividades) {
    Inscripcion inscripcion = Inscripcion.builder()
        .alumno(alumno)
        .actividad(actividad)
        .sede(null) // se asigna después, cuando el admin confirma
        .sedesSugeridas(sedesSugeridas)
        .estado(EstadoInscripcion.PENDIENTE)
        .anioParticipacion(request.getAnioParticipacion())
        .whatsappContacto(request.getWhatsappContacto())
        .retiroMenor(request.getRetiroMenor())
        .quienBusca(request.getQuienBusca())
        .autorizaActividad(request.isAutorizaActividad())
        .firmaActividad(request.getFirmaActividad())
        .autorizaImagen(request.isAutorizaImagen())
        .firmaImagen(request.getFirmaImagen())
        .build();
    ultimaInscripcion = inscripcionRepository.save(inscripcion);
        }

        return mapearADTO(ultimaInscripcion);
    }

    @Override
    public List<InscripcionDTO> listarTodas() {
        return inscripcionRepository.findAll()
            .stream()
            .map(this::mapearADTO)
            .toList();
    }

    @Override
    public InscripcionDTO cambiarEstado(Long id, EstadoInscripcion nuevoEstado) {
    Inscripcion inscripcion = inscripcionRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Inscripción no encontrada: " + id));

    // Solo valida cupo al CONFIRMAR - es el único estado que reserva un lugar real
    if (nuevoEstado == EstadoInscripcion.CONFIRMADO) {
        Actividad actividad = inscripcion.getActividad();

        long confirmadas = inscripcionRepository
            .countByActividad_IdAndEstado(actividad.getId(), EstadoInscripcion.CONFIRMADO);

        if (actividad.getCupoMax() != null && confirmadas >= actividad.getCupoMax()) {
            throw new IllegalStateException(
                "No hay cupos disponibles para " + actividad.getNombre()
            );
        }
    }

    inscripcion.setEstado(nuevoEstado);
    inscripcion.getAlumno().setEstado(nuevoEstado);
    alumnoRepository.save(inscripcion.getAlumno());

    Inscripcion actualizada = inscripcionRepository.save(inscripcion);
    return mapearADTO(actualizada);
    }

    @Override
    public InscripcionDTO asignarSede(Long idInscripcion, Long idSede) {
        Inscripcion inscripcion = inscripcionRepository.findById(idInscripcion)
            .orElseThrow(() -> new EntityNotFoundException("Inscripción no encontrada: " + idInscripcion));

        Sede sede = sedeRepository.findById(idSede)
            .orElseThrow(() -> new EntityNotFoundException("Sede no encontrada: " + idSede));

        inscripcion.setSede(sede);
        Inscripcion actualizada = inscripcionRepository.save(inscripcion);
        return mapearADTO(actualizada);
    }

    @Override
    public List<InscripcionDTO> listarPorActividad(Long idActividad) {
        return inscripcionRepository.findByActividadId(idActividad)
            .stream()
            .map(this::mapearADTO)
            .toList();
    }

    private Set<Sede> buscarSedes(List<Long> idsSedes) {
        if (idsSedes == null || idsSedes.isEmpty()) return new HashSet<>();
        List<Sede> encontradas = sedeRepository.findAllById(idsSedes);
        if (encontradas.size() != idsSedes.size()) {
            List<Long> idsEncontrados = encontradas.stream().map(Sede::getId).toList();
            List<Long> idsFaltantes = idsSedes.stream()
            .filter(id -> !idsEncontrados.contains(id))
            .toList();
        throw new EntityNotFoundException("No se encontraron las sedes con id: " + idsFaltantes);
        }

    return new HashSet<>(encontradas);
}
    // Entity -> DTO 
    private InscripcionDTO mapearADTO(Inscripcion inscripcion) {
        Alumno alumno = inscripcion.getAlumno();
        Tutor tutor = alumno.getTutor();

        return InscripcionDTO.builder()
            .id(inscripcion.getId())
            .nombreAlumno(alumno.getNombre() + " " + alumno.getApellido())
            .dniAlumno(alumno.getDni())
            .actividades(List.of(inscripcion.getActividad().getNombre()))
            .sedes(inscripcion.getSede() != null
                ? List.of(inscripcion.getSede().getNombre())
                : inscripcion.getSedesSugeridas().stream().map(Sede::getNombre).toList())
            .nombreTutor(tutor.getNombre() + " " + tutor.getApellido())
            .telefonoTutor(tutor.getTelefono())
            .fechaInscripcion(inscripcion.getFechaInscripcion())
            .estado(inscripcion.getEstado())
            .build();
    }
}
