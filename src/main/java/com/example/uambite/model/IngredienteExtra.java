package com.example.uambite.model;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "ingrediente_extra")
public class IngredienteExtra extends BaseEntity {

    private String nombre;

    private Double precioExtra;

    @OneToMany(mappedBy = "ingredienteExtra")
    private List<ProductoIngredienteExtra> productos;

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Double getPrecioExtra() {
        return precioExtra;
    }

    public void setPrecioExtra(Double precioExtra) {
        this.precioExtra = precioExtra;
    }
}