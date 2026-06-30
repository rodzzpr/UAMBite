package com.example.uambite.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "pago")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Pago extends BaseEntity {

    @Enumerated(EnumType.STRING)
    @Column(name = "metodo_pago", nullable = false, length = 30)
    private MetodoPago metodoPago;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal monto;

    private LocalDateTime fecha;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private EstadoPago estado;

    @JsonBackReference(value = "pedido-pago")
    @OneToOne
    @JoinColumn(name = "pedido_id", unique = true)
    private Pedido pedido;
}
