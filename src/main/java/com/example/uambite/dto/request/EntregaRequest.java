package com.example.uambite.dto.request;

import java.util.UUID;

public class EntregaRequest {

    private String ubicacion;
    private UUID pedidoId;

    public String getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }

    public UUID getPedidoId() {
        return pedidoId;
    }

    public void setPedidoId(UUID pedidoId) {
        this.pedidoId = pedidoId;
    }
}