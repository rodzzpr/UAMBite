package com.example.uambite.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "detalle_pedido_ingrediente_extra")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DetallePedidoIngredienteExtra extends BaseEntity {

    @JsonBackReference(value = "detalle-ingrediente-extra")
    @ManyToOne
    @JoinColumn(name = "detalle_pedido_id", nullable = false)
    private DetallePedido detallePedido;

    @ManyToOne
    @JoinColumn(name = "ingrediente_extra_id", nullable = false)
    private IngredienteExtra ingredienteExtra;

    @Column(name = "precio_adicional", nullable = false)
    private Double precioAdicional;
}
