package com.example.uambite.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class IngredienteExtraRequest {

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @NotNull(message = "El precio extra es obligatorio")
    @Positive(message = "El precio extra debe ser mayor a cero")
    private Double precioExtra;

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
