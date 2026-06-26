package com.example.uambite.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;
import java.util.UUID;

public class DescuentoRequest {

    @NotBlank(message = "El código es obligatorio")
    private String codigo;

    @NotNull(message = "El porcentaje es obligatorio")
    @Positive(message = "El porcentaje debe ser mayor a cero")
    private Double porcentaje;

    @NotNull(message = "La fecha de vencimiento es obligatoria")
    private LocalDate fechaVencimiento;

    @NotNull(message = "El local de comida es obligatorio")
    private UUID localComidaId;

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public Double getPorcentaje() {
        return porcentaje;
    }

    public void setPorcentaje(Double porcentaje) {
        this.porcentaje = porcentaje;
    }

    public LocalDate getFechaVencimiento() {
        return fechaVencimiento;
    }

    public void setFechaVencimiento(LocalDate fechaVencimiento) {
        this.fechaVencimiento = fechaVencimiento;
    }

    public UUID getLocalComidaId() {
        return localComidaId;
    }

    public void setLocalComidaId(UUID localComidaId) {
        this.localComidaId = localComidaId;
    }
}
