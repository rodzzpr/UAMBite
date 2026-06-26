package com.example.uambite.dto.request;

public class IngredienteExtraRequest {

    private String nombre;

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