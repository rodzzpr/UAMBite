package com.example.uambite.model;

import jakarta.persistence.*;

@Entity
@Table(name = "producto_ingrediente_extra")
public class ProductoIngredienteExtra extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "producto_id")
    private Producto producto;

    @ManyToOne
    @JoinColumn(name = "ingrediente_extra_id")
    private IngredienteExtra ingredienteExtra;

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    public IngredienteExtra getIngredienteExtra() {
        return ingredienteExtra;
    }

    public void setIngredienteExtra(IngredienteExtra ingredienteExtra) {
        this.ingredienteExtra = ingredienteExtra;
    }
}