package com.example.uambite.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "producto_ingrediente_extra")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductoIngredienteExtra extends BaseEntity {

    @JsonBackReference(value = "producto-ingrediente-extra")
    @ManyToOne
    @JoinColumn(name = "producto_id")
    private Producto producto;

    @JsonBackReference(value = "ingrediente-producto")
    @ManyToOne
    @JoinColumn(name = "ingrediente_extra_id")
    private IngredienteExtra ingredienteExtra;
}
