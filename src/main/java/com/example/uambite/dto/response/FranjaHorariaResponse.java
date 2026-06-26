package com.example.uambite.dto.response;

import java.time.LocalTime;
import java.util.UUID;

public class FranjaHorariaResponse {

    private UUID id;

    private LocalTime horaInicio;

    private LocalTime horaFin;

    private Integer capacidadMaxima;

    private Integer pedidosActuales;

    private Boolean disponible;

    private String localComida;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

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

    public String getLocalComida() {
        return localComida;
    }

    public void setLocalComida(String localComida) {
        this.localComida = localComida;
    }
}
