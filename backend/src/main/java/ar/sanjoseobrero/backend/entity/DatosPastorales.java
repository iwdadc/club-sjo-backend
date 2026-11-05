package ar.sanjoseobrero.backend.entity;

import ar.sanjoseobrero.backend.entity.enums.RazonSacramento;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "datos_pastorales")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DatosPastorales {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //Sacramentos recibidos
    private Boolean bautismo;
    private Boolean comunion;
    private Boolean confirmacion;

    @Enumerated(EnumType.STRING)
    @Column(name = "razon_sacramento", length = 30)
    private RazonSacramento razonSacramento;

    // Grupos pastorales - un alumno puede estar en varios
    // @ElementCollection crea una tabla aparte (datos_pastorales_grupos)
    // sin necesidad de una entidad completa, ya que son solo strings
    @ElementCollection
    @CollectionTable(
        name = "datos_pastorales_grupos",
        joinColumns = @JoinColumn(name = "id_datos_pastorales")
    )
    @Column(name = "grupo")
    @Builder.Default
    private List<String> gruposPastorales = new ArrayList<>();

    //Relación con Alumno
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_alumno", nullable = false, unique = true)
    private Alumno alumno;
}