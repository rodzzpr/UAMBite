package com.example.uambite.model;

import jakarta.persistence.*;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "ingrediente_extra")
public class IngredienteExtra {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String nombre;

    private Double precioExtra;

    @OneToMany(mappedBy = "ingredienteExtra")
    private List<ProductoIngredienteExtra> productos;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

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