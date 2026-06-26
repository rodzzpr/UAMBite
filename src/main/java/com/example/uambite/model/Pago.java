package com.example.uambite.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "pago")
public class Pago extends BaseEntity {

    @Enumerated(EnumType.STRING)
    private MetodoPago metodoPago;

    private Double monto;

    private LocalDateTime fecha;

    @Enumerated(EnumType.STRING)
    private EstadoPago estado;

    @OneToOne
    @JoinColumn(name = "pedido_id")
    @JsonBackReference(value = "pedido-pago")
    private Pedido pedido;

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

    public Pedido getPedido() {
        return pedido;
    }

    public void setPedido(Pedido pedido) {
        this.pedido = pedido;
    }
}
