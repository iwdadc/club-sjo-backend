package ar.sanjoseobrero.backend.entity;
import ar.sanjoseobrero.backend.entity.enums.AnioParticipacion;
import ar.sanjoseobrero.backend.entity.enums.EstadoInscripcion;
import ar.sanjoseobrero.backend.entity.enums.RetiroMenor;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDateTime;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table (name = "inscripciones")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class Inscripcion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "fecha_inscripcion", nullable = false, updatable = false)
    private LocalDateTime fechaInscripcion;

    @PrePersist
    protected void onCreate() {
        fechaInscripcion = LocalDateTime.now();
    }

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private EstadoInscripcion estado = EstadoInscripcion.PENDIENTE;

    @Enumerated(EnumType.STRING)
    @Column(name = "anio_participacion", length = 20)
    private AnioParticipacion anioParticipacion;

    @NotBlank(message = "El contacto de WhatsApp es obligatorio")
    @Column(name = "whatsapp_contacto", nullable = false)
    private String whatsappContacto;

    @Enumerated(EnumType.STRING)
    @Column(name = "retiro_menor", length = 20)
    private RetiroMenor retiroMenor;

    // Solo si retiroMenor == LO_BUSCAN
    @Column(name = "quien_busca")
    private String quienBusca;

    // ── Autorizaciones ──
    @Builder.Default
    private Boolean autorizaActividad = false;

    @Column(name = "firma_actividad")
    private String firmaActividad;

    @Builder.Default
    private Boolean autorizaImagen = false;

    @Column(name = "firma_imagen")
    private String firmaImagen;

    // RELACIONES 

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_alumno", nullable = false)
    private Alumno alumno;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_actividad", nullable = false)
    private Actividad actividad;

    // Sede definitiva - la asigna el admin al confirmar, arranca en null
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_sede")
    private Sede sede;

    // Sedes que el alumno sugirió en el formulario - no son definitivas
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "inscripcion_sedes_sugeridas",
        joinColumns = @JoinColumn(name = "id_inscripcion"),
        inverseJoinColumns = @JoinColumn(name = "id_sede")
    )
    @Builder.Default
    private Set<Sede> sedesSugeridas = new HashSet<>();
}
