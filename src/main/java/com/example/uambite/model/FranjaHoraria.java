package com.example.uambite.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import java.time.LocalTime;
import java.util.List;

@Entity
@Table(name = "franja_horaria")
public class FranjaHoraria extends BaseEntity {

    private LocalTime horaInicio;

    private LocalTime horaFin;

    private Integer capacidadMaxima;

    private Integer pedidosActuales;

    private Boolean disponible;

    @ManyToOne
    @JoinColumn(name = "local_comida_id")
    @JsonBackReference(value = "local-franja")
    private LocalComida localComida;

    @OneToMany(mappedBy = "franjaHoraria")
    @JsonManagedReference(value = "franja-pedido")
    private List<Pedido> pedidos;

    // ==========================
    // Getters y Setters
    // ==========================

    public LocalTime getHoraInicio() {
        return horaInicio;
    }

    public void setHoraInicio(LocalTime horaInicio) {
        this.horaInicio = horaInicio;
    }

    public LocalTime getHoraFin() {
        return horaFin;
    }

    public void setHoraFin(LocalTime horaFin) {
        this.horaFin = horaFin;
    }

    public Integer getCapacidadMaxima() {
        return capacidadMaxima;
    }

    public void setCapacidadMaxima(Integer capacidadMaxima) {
        this.capacidadMaxima = capacidadMaxima;
    }

    public Integer getPedidosActuales() {
        return pedidosActuales;
    }

    public void setPedidosActuales(Integer pedidosActuales) {
        this.pedidosActuales = pedidosActuales;
    }

    public Boolean getDisponible() {
        return disponible;
    }

    public void setDisponible(Boolean disponible) {
        this.disponible = disponible;
    }

    public LocalComida getLocalComida() {
        return localComida;
    }

    public void setLocalComida(LocalComida localComida) {
        this.localComida = localComida;
    }

    public List<Pedido> getPedidos() {
        return pedidos;
    }

    public void setPedidos(List<Pedido> pedidos) {
        this.pedidos = pedidos;
    }
}
