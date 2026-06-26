package com.example.uambite.dto.response;

import java.util.UUID;

public class IngredienteExtraResponse {

    private UUID id;

    private String nombre;

    private Double precioExtra;

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