package com.example.uambite.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "pedido")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Pedido extends BaseEntity {

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private EstadoPedido estado;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal total;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_entrega", nullable = false, length = 30)
    private TipoEntrega tipoEntrega;

    @Column(name = "local_comida_id")
    private UUID localComidaId;

    @Column
    @Builder.Default
    private Integer prioridad = 0;

    @JsonBackReference(value = "usuario-pedido")
    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    @JsonManagedReference(value = "pedido-detalle")
    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DetallePedido> detalles;

    @OneToOne(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference(value = "pedido-pago")
    private Pago pago;

    @OneToOne(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference(value = "pedido-entrega")
    private Entrega entrega;

    @ManyToOne
    @JoinColumn(name = "descuento_id")
    private Descuento descuento;

    @JsonBackReference(value = "franja-pedido")
    @ManyToOne
    @JoinColumn(name = "franja_id")
    private FranjaHoraria franjaHoraria;
}

