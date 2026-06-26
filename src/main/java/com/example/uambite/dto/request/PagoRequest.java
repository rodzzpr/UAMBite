package com.example.uambite.dto.request;

import com.example.uambite.model.MetodoPago;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public class PagoRequest {

    @NotNull(message = "El método de pago es obligatorio")
    private MetodoPago metodoPago;

    @NotNull(message = "El pedido es obligatorio")
    private UUID pedidoId;

    public MetodoPago getMetodoPago() {
        return metodoPago;
    }

    public void setMetodoPago(MetodoPago metodoPago) {
        this.metodoPago = metodoPago;
    }

    public UUID getPedidoId() {
        return pedidoId;
    }

    public void setPedidoId(UUID pedidoId) {
        this.pedidoId = pedidoId;
    }
}
