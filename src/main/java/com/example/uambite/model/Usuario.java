package com.example.uambite.model;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import java.util.List;

import jakarta.persistence.*;

@Entity
@Table(name = "usuario")
public class Usuario extends BaseEntity {

    private String nombre;

    private String correo;

    private String rol;

    @OneToMany(mappedBy = "usuario")
    @JsonManagedReference(value = "usuario-pedido")
    private List<Pedido> pedidos;

    // Getters y Setters

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public List<Pedido> getPedidos() {
        return pedidos;
    }

    public void setPedidos(List<Pedido> pedidos) {
        this.pedidos = pedidos;
    }
}