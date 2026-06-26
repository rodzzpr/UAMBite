package com.example.uambite.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "descuento")
public class Descuento extends BaseEntity {

    private String codigo;

    private Double porcentaje;

    private Boolean activo;

    private LocalDate fechaVencimiento;

    @JsonBackReference(value = "local-descuento")
    @ManyToOne
    @JoinColumn(name = "local_comida_id")
    private LocalComida localComida;

    // getters y setters

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

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }

    public LocalDate getFechaVencimiento() {
        return fechaVencimiento;
    }

    public void setFechaVencimiento(LocalDate fechaVencimiento) {
        this.fechaVencimiento = fechaVencimiento;
    }

    public LocalComida getLocalComida() {
        return localComida;
    }

    public void setLocalComida(LocalComida localComida) {
        this.localComida = localComida;
    }
}