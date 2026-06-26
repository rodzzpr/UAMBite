package com.example.uambite.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "entrega")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Entrega extends BaseEntity {

    @Column(length = 255)
    private String ubicacion;

    @Column(name = "fecha_entrega")
    private LocalDateTime fechaEntrega;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private EstadoEntrega estado;

    @JsonBackReference(value = "pedido-entrega")
    @OneToOne
    @JoinColumn(name = "pedido_id", unique = true)
    private Pedido pedido;
}
