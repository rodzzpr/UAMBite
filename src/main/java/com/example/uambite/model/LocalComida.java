package com.example.uambite.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "local_comida")
public class LocalComida extends BaseEntity {

    private String nombre;

    private String ubicacion;

    private String horario;

    private Boolean disponible;

    @OneToMany(mappedBy = "localComida")
    @JsonManagedReference(value = "local-producto")
    private List<Producto> productos;

    @OneToMany(mappedBy = "localComida")
    @JsonManagedReference(value = "local-descuento")
    private List<Descuento> descuentos;

    @OneToMany(mappedBy = "localComida")
    @JsonManagedReference(value = "local-franja")
    private List<FranjaHoraria> franjasHorarias;

    // ==========================
    // Getters y Setters
    // ==========================

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }

    public String getHorario() {
        return horario;
    }

    public void setHorario(String horario) {
        this.horario = horario;
    }

    public Boolean getDisponible() {
        return disponible;
    }

    public void setDisponible(Boolean disponible) {
        this.disponible = disponible;
    }

    public List<Producto> getProductos() {
        return productos;
    }

    public void setProductos(List<Producto> productos) {
        this.productos = productos;
    }

    public List<Descuento> getDescuentos() {
        return descuentos;
    }

    public void setDescuentos(List<Descuento> descuentos) {
        this.descuentos = descuentos;
    }

    public List<FranjaHoraria> getFranjasHorarias() {
        return franjasHorarias;
    }

    public void setFranjasHorarias(List<FranjaHoraria> franjasHorarias) {
        this.franjasHorarias = franjasHorarias;
    }
}