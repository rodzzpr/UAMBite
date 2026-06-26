package com.example.uambite.dto.response;

import java.util.UUID;

public class ProductoResponse {

    private UUID id;

    private String nombre;
    private String descripcion;
    private Double precio;
    private Integer stock;

    // Solo mostramos el nombre del local
    private String localComida;

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

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Double getPrecio() {
        return precio;
    }

    public void setPrecio(Double precio) {
        this.precio = precio;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public String getLocalComida() {
        return localComida;
    }

    public void setLocalComida(String localComida) {
        this.localComida = localComida;
    }
}