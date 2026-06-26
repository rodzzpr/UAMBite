package com.example.uambite.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "producto")
public class Producto extends BaseEntity {

    private String nombre;

    private String descripcion;

    private Double precio;

    private Integer stock;

    // Relación con LocalComida
    @JsonBackReference(value = "local-producto")
    @ManyToOne
    @JoinColumn(name = "local_id")
    private LocalComida localComida;

    // Relación con DetallePedido
    @JsonManagedReference(value = "producto-detalle")
    @OneToMany(mappedBy = "producto")
    private List<DetallePedido> detalles;

    // Relación de IngredienteExtra con Producto
    @OneToMany(mappedBy = "producto")
    private List<ProductoIngredienteExtra> ingredientesExtras;

    // Getters y Setters

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Double getPrecio() {
        return precio;
    }

    public void setPrecio(Double precio) {
        this.precio = precio;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public LocalComida getLocalComida() {
        return localComida;
    }

    public void setLocalComida(LocalComida localComida) {
        this.localComida = localComida;
    }

    public List<DetallePedido> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<DetallePedido> detalles) {
        this.detalles = detalles;
    }

    public List<ProductoIngredienteExtra> getIngredientesExtras() {
        return ingredientesExtras;
    }

    public void setIngredientesExtras(List<ProductoIngredienteExtra> ingredientesExtras) {
        this.ingredientesExtras = ingredientesExtras;
    }
}