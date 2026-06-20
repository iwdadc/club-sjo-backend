package ar.sanjoseobrero.backend.entity;
import ar.sanjoseobrero.backend.entity.enums.AnioParticipacion;
import ar.sanjoseobrero.backend.entity.enums.EstadoInscripcion;
import ar.sanjoseobrero.backend.entity.enums.RetiroMenor;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDateTime;

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

    // Sede específica donde se realizará ESTA inscripción
    // El admin la asigna después según disponibilidad
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_sede")
    private Sede sede;
}
