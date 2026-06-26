package com.example.uambite.service;

import com.example.uambite.dto.request.ProductoRequest;
import com.example.uambite.dto.response.ProductoResponse;
import com.example.uambite.model.LocalComida;
import com.example.uambite.model.Producto;
import com.example.uambite.repository.LocalComidaRepository;
import com.example.uambite.repository.ProductoRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductoService {

    private final ProductoRepository repository;
    private final LocalComidaRepository localRepository;

    public ProductoService(ProductoRepository repository,
                           LocalComidaRepository localRepository) {
        this.repository = repository;
        this.localRepository = localRepository;
    }

    // Obtener todos los productos
    public List<ProductoResponse> getAll() {
        return repository.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // Guardar producto
    public ProductoResponse save(ProductoRequest request) {

        LocalComida local = localRepository.findById(request.getLocalComidaId())
                .orElseThrow(() -> new EntityNotFoundException("Local de comida no encontrado"));

        Producto producto = new Producto();

        producto.setNombre(request.getNombre());
        producto.setDescripcion(request.getDescripcion());
        producto.setPrecio(request.getPrecio());
        producto.setStock(request.getStock());
        producto.setLocalComida(local);

        Producto saved = repository.save(producto);

        return toResponse(saved);
    }

    // Conversión Entity -> DTO
    private ProductoResponse toResponse(Producto producto) {

        ProductoResponse response = new ProductoResponse();

        response.setId(producto.getId());
        response.setNombre(producto.getNombre());
        response.setDescripcion(producto.getDescripcion());
        response.setPrecio(producto.getPrecio());
        response.setStock(producto.getStock());

        if (producto.getLocalComida() != null) {
            response.setLocalComida(producto.getLocalComida().getNombre());
        }

        return response;
    }
}