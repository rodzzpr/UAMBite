package com.example.uambite.dto.request;

import com.example.uambite.model.TipoEntrega;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public class PedidoRequest {

    @NotNull(message = "El tipo de entrega es obligatorio")
    private TipoEntrega tipoEntrega;

    @NotNull(message = "El usuario es obligatorio")
    private UUID usuarioId;

    private UUID franjaHorariaId;

    private UUID descuentoId;

    public TipoEntrega getTipoEntrega() {
        return tipoEntrega;
    }

    public void setTipoEntrega(TipoEntrega tipoEntrega) {
        this.tipoEntrega = tipoEntrega;
    }

    public UUID getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(UUID usuarioId) {
        this.usuarioId = usuarioId;
    }

    public UUID getFranjaHorariaId() {
        return franjaHorariaId;
    }

    public void setFranjaHorariaId(UUID franjaHorariaId) {
        this.franjaHorariaId = franjaHorariaId;
    }

    public UUID getDescuentoId() {
        return descuentoId;
    }

    public void setDescuentoId(UUID descuentoId) {
        this.descuentoId = descuentoId;
    }
}
