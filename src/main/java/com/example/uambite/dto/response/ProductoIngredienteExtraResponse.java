package com.example.uambite.dto.response;

import java.util.UUID;

public class ProductoIngredienteExtraResponse {

    private UUID id;

    private String producto;

    private String ingredienteExtra;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getProducto() {
        return producto;
    }

    public void setProducto(String producto) {
        this.producto = producto;
    }

    public String getIngredienteExtra() {
        return ingredienteExtra;
    }

    public void setIngredienteExtra(String ingredienteExtra) {
        this.ingredienteExtra = ingredienteExtra;
    }
}