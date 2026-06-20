package ar.sanjoseobrero.backend.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "datos_salud")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DatosSalud {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Obra social
    @Column(name = "tiene_obra_social")
    private Boolean tieneObraSocial;

    @Column(name = "nombre_obra_social")
    private String nombreObraSocial;

    @Column(name = "nro_afiliado", length = 50)
    private String nroAfiliado;

    // ── Condiciones de salud ──
    private Boolean asma;
    private Boolean diabetes;
    private Boolean hipertension;
    private Boolean hipotension;

    @Column(name = "problemas_cardiacos")
    private Boolean problemasCardiacos;

    private Boolean celiaquia;

    private Boolean alergias;
    @Column(name = "detalle_alergias", columnDefinition = "TEXT")
    private String detalleAlergias;

    private Boolean epilepsia;

    @Column(name = "problemas_columna")
    private Boolean problemasColumna;
    @Column(name = "detalle_columna", columnDefinition = "TEXT")
    private String detalleColumna;

    @Column(name = "problemas_huesos")
    private Boolean problemasHuesos;

    private Boolean convulsiones;

    @Column(name = "condicion_alimentaria")
    private Boolean condicionAlimentaria;
    @Column(name = "detalle_alimentaria", columnDefinition = "TEXT")
    private String detalleAlimentaria;

    // Durante el ejercicio
    private Boolean desmayos;
    private Boolean mareos;
    private Boolean palpitaciones;

    @Column(name = "dolor_pecho")
    private Boolean dolorPecho;

    @Column(name = "mayor_cansancio")
    private Boolean mayorCansancio;

    @Column(name = "dificultad_respirar")
    private Boolean dificultadRespirar;

    // ── Otros ──
    @Column(name = "disminucion_auditiva")
    private Boolean disminucionAuditiva;
    @Column(name = "detalle_auditivo", columnDefinition = "TEXT")
    private String detalleAuditivo;

    @Column(name = "dificultad_visual")
    private Boolean dificultadVisual;
    @Column(name = "detalle_visual", columnDefinition = "TEXT")
    private String detalleVisual;

    private Boolean medicacion;
    @Column(name = "detalle_medicacion", columnDefinition = "TEXT")
    private String detalleMedicacion;

    private Boolean operacion;
    @Column(name = "detalle_operacion", columnDefinition = "TEXT")
    private String detalleOperacion;

    @Column(name = "otras_observaciones", columnDefinition = "TEXT")
    private String otrasObservaciones;

    //Relación con Alumno
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_alumno", nullable = false, unique = true)
    private Alumno alumno;
}