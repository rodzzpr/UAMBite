package com.example.uambite.service;

import com.example.uambite.model.Producto;
import com.example.uambite.repository.ProductoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductoService {

    private final ProductoRepository repository;

    public ProductoService(ProductoRepository repository) {
        this.repository = repository;
    }

    public List<Producto> getAll() {
        return repository.findAll();
    }

    public Producto save(Producto producto) {
        return repository.save(producto);
    }
}