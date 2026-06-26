package com.example.uambite.dto.request;

import java.time.LocalTime;
import java.util.UUID;

public class FranjaHorariaRequest {

    private LocalTime horaInicio;

    private LocalTime horaFin;

    private Integer capacidadMaxima;

    private UUID localComidaId;

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

    public UUID getLocalComidaId() {
        return localComidaId;
    }

    public void setLocalComidaId(UUID localComidaId) {
        this.localComidaId = localComidaId;
    }
}
