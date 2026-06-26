package com.example.uambite.model;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "producto_ingrediente_extra")
public class ProductoIngredienteExtra {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "producto_id")
    private Producto producto;

    @ManyToOne
    @JoinColumn(name = "ingrediente_extra_id")
    private IngredienteExtra ingredienteExtra;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

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