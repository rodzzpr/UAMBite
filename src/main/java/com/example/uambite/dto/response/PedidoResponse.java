package com.example.uambite.dto.response;

import com.example.uambite.model.EstadoPedido;
import com.example.uambite.model.TipoEntrega;

import java.util.UUID;

public class PedidoResponse {

    private UUID id;
    private EstadoPedido estado;
    private Double total;
    private TipoEntrega tipoEntrega;
    private String usuario;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

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

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }
}
