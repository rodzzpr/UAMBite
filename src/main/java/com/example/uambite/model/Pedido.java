package com.example.uambite.model;
import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import java.util.List;

@Entity
@Table(name = "pedido")
public class Pedido extends BaseEntity {

    @Enumerated(EnumType.STRING)
    private EstadoPedido estado;

    private Double total;

    @Enumerated(EnumType.STRING)
    private TipoEntrega tipoEntrega;

    @JsonBackReference(value = "usuario-pedido")
    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    @OneToMany(mappedBy = "pedido")
    @JsonManagedReference(value = "pedido-detalle")
    private List<DetallePedido> detalles;

    @OneToOne(mappedBy = "pedido")
    @JsonManagedReference(value = "pedido-pago")
    private Pago pago;

    @OneToOne(mappedBy = "pedido")
    @JsonManagedReference(value = "pedido-entrega")
    private Entrega entrega;

    @ManyToOne
    @JoinColumn(name = "descuento_id")
    private Descuento descuento;

    @ManyToOne
    @JoinColumn(name = "franja_id")
    @JsonBackReference(value = "franja-pedido")
    private FranjaHoraria franjaHoraria;

    // Getters y Setters

    public EstadoPedido getEstado() {
        return estado;
    }

    public void setEstado(EstadoPedido estado) {
        this.estado = estado;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }

    public TipoEntrega getTipoEntrega() {
        return tipoEntrega;
    }

    public void setTipoEntrega(TipoEntrega tipoEntrega) {
        this.tipoEntrega = tipoEntrega;
    }

    public List<DetallePedido> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<DetallePedido> detalles) {
        this.detalles = detalles;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Pago getPago() {
        return pago;
    }

    public void setPago(Pago pago) {
        this.pago = pago;
    }

    public Entrega getEntrega() {
        return entrega;
    }

    public void setEntrega(Entrega entrega) {
        this.entrega = entrega;
    }

    public Descuento getDescuento() {
        return descuento;
    }

    public void setDescuento(Descuento descuento) {
        this.descuento = descuento;
    }

    public FranjaHoraria getFranjaHoraria() {
        return franjaHoraria;
    }

    public void setFranjaHoraria(FranjaHoraria franjaHoraria) {
        this.franjaHoraria = franjaHoraria;
    }
}
