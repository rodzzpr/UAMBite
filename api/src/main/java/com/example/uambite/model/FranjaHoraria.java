package com.example.uambite.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalTime;
import java.util.List;

@Entity
@Table(name = "franja_horaria")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FranjaHoraria extends BaseEntity {

    @Column(name = "hora_inicio", nullable = false)
    private LocalTime horaInicio;

    @Column(name = "hora_fin", nullable = false)
    private LocalTime horaFin;

    @Column(name = "capacidad_maxima", nullable = false)
    private Integer capacidadMaxima;

    @Column(name = "pedidos_actuales", nullable = false)
    private Integer pedidosActuales;

    @Column(nullable = false)
    private Boolean disponible;

    @Version
    private Long version;

    @JsonBackReference(value = "local-franja")
    @ManyToOne
    @JoinColumn(name = "local_comida_id")
    private LocalComida localComida;

    @JsonManagedReference(value = "franja-pedido")
    @OneToMany(mappedBy = "franjaHoraria")
    private List<Pedido> pedidos;
}
