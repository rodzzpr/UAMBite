package com.example.uambite.dto.request;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public class ProductoIngredienteExtraRequest {

    @NotNull(message = "El producto es obligatorio")
    private UUID productoId;

    @NotNull(message = "El ingrediente extra es obligatorio")
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
