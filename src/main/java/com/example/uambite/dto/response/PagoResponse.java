package com.example.uambite.dto.response;

import com.example.uambite.model.EstadoPago;
import com.example.uambite.model.MetodoPago;

import java.time.LocalDateTime;
import java.util.UUID;

public class PagoResponse {

    private UUID id;
    private MetodoPago metodoPago;
    private Double monto;
    private LocalDateTime fecha;
    private EstadoPago estado;
    private UUID pedidoId;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public MetodoPago getMetodoPago() {
        return metodoPago;
    }

    public void setMetodoPago(MetodoPago metodoPago) {
        this.metodoPago = metodoPago;
    }

    public Double getMonto() {
        return monto;
    }

    public void setMonto(Double monto) {
        this.monto = monto;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public EstadoPago getEstado() {
        return estado;
    }

    public void setEstado(EstadoPago estado) {
        this.estado = estado;
    }

    public UUID getPedidoId() {
        return pedidoId;
    }

    public void setPedidoId(UUID pedidoId) {
        this.pedidoId = pedidoId;
    }
}
