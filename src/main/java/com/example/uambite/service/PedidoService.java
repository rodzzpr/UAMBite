package com.example.uambite.service;

import com.example.uambite.model.Pedido;
import com.example.uambite.repository.PedidoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PedidoService {

    private final PedidoRepository repository;

    public PedidoService(PedidoRepository repository) {
        this.repository = repository;
    }

    public List<Pedido> getAll() {
        return repository.findAll();
    }

    public Pedido save(Pedido pedido) {
        return repository.save(pedido);
    }
}