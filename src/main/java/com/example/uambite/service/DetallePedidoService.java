package com.example.uambite.service;

import com.example.uambite.model.DetallePedido;
import com.example.uambite.repository.DetallePedidoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DetallePedidoService {

    private final DetallePedidoRepository repository;

    public DetallePedidoService(DetallePedidoRepository repository) {
        this.repository = repository;
    }

    public List<DetallePedido> getAll() {
        return repository.findAll();
    }

    public DetallePedido save(DetallePedido detallePedido) {
        return repository.save(detallePedido);
    }

}