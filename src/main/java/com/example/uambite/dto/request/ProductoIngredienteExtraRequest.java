package com.example.uambite.dto.request;

import java.util.UUID;

public class ProductoIngredienteExtraRequest {

    private UUID productoId;

    private UUID ingredienteExtraId;

    public UUID getProductoId() {
        return productoId;
    }

    public void setProductoId(UUID productoId) {
        this.productoId = productoId;
    }

    public UUID getIngredienteExtraId() {
        return ingredienteExtraId;
    }

    public void setIngredienteExtraId(UUID ingredienteExtraId) {
        this.ingredienteExtraId = ingredienteExtraId;
    }
}