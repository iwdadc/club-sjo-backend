// DatosPastorales.java - Datos religiosos/pastorales del alumno

package ar.sanjoseobrero.backend.entity;

import ar.sanjoseobrero.backend.entity.enums.Escolaridad;
import ar.sanjoseobrero.backend.entity.enums.Turno;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "alumnos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Alumno  extends Persona{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "foto_dni_frente_url")
    private String fotoDniFrenteUrl;

    @Column(name = "foto_dni_dorso_url")
    private String fotoDniDorsoUrl;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private Escolaridad escolaridad;

    private String escuela;

    @Column(name = "grado_division", length = 20)
    private String gradoDivision;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private Turno turno;

    private String ocupacion;

    @Column(columnDefinition = "TEXT")
    private String convivencia;

    //Situación familiar - datos opcionales del alumno 
    @Column(name = "nombre_padre")
    private String nombrePadre;

    @Column(name = "apellido_padre")
    private String apellidoPadre;

    @Column(name = "dni_padre", length = 20)
    private String dniPadre;

    @Column(name = "nombre_madre")
    private String nombreMadre;

    @Column(name = "apellido_madre")
    private String apellidoMadre;

    @Column(name = "dni_madre", length = 20)
    private String dniMadre;

    @Column(name = "fecha_registro", nullable = false, updatable = false)
    private LocalDateTime fechaRegistro;

    @PrePersist
    protected void onCreate() {
        fechaRegistro = LocalDateTime.now();
    }

    // RELACIONES 

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_tutor", nullable = false)
    private Tutor tutor;

    @OneToOne(mappedBy = "alumno", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private DatosSalud datosSalud;

    @OneToOne(mappedBy = "alumno", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private DatosPastorales datosPastorales;

    @OneToMany(mappedBy = "alumno", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Inscripcion> inscripciones = new ArrayList<>();
}
