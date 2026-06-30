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
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "detalle_pedido")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DetallePedido extends BaseEntity {

    @Column(nullable = false)
    private Integer cantidad;

    @Column(name = "precio_unitario", nullable = false, precision = 12, scale = 2)
    private BigDecimal precioUnitario;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal subtotal;

    @JsonBackReference(value = "pedido-detalle")
    @ManyToOne
    @JoinColumn(name = "pedido_id")
    private Pedido pedido;

    @JsonBackReference(value = "producto-detalle")
    @ManyToOne
    @JoinColumn(name = "producto_id")
    private Producto producto;

    @Builder.Default
    @JsonManagedReference(value = "detalle-ingrediente-extra")
    @OneToMany(mappedBy = "detallePedido", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DetallePedidoIngredienteExtra> ingredientesExtra = new ArrayList<>();
}
