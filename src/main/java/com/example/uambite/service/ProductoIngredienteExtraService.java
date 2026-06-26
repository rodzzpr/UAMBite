package com.example.uambite.service;

import com.example.uambite.dto.request.ProductoIngredienteExtraRequest;
import com.example.uambite.dto.response.ProductoIngredienteExtraResponse;
import com.example.uambite.model.IngredienteExtra;
import com.example.uambite.model.Producto;
import com.example.uambite.model.ProductoIngredienteExtra;
import com.example.uambite.repository.IngredienteExtraRepository;
import com.example.uambite.repository.ProductoIngredienteExtraRepository;
import com.example.uambite.repository.ProductoRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductoIngredienteExtraService {

    private final ProductoIngredienteExtraRepository repository;
    private final ProductoRepository productoRepository;
    private final IngredienteExtraRepository ingredienteRepository;

    public ProductoIngredienteExtraService(
            ProductoIngredienteExtraRepository repository,
            ProductoRepository productoRepository,
            IngredienteExtraRepository ingredienteRepository) {

        this.repository = repository;
        this.productoRepository = productoRepository;
        this.ingredienteRepository = ingredienteRepository;
    }

    // Obtener todas las relaciones
    public List<ProductoIngredienteExtraResponse> getAll() {
        return repository.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // Asociar un ingrediente a un producto
    @Transactional
    public ProductoIngredienteExtraResponse save(ProductoIngredienteExtraRequest request) {

        Producto producto = productoRepository.findById(request.getProductoId())
                .orElseThrow(() -> new EntityNotFoundException("Producto no encontrado."));

        IngredienteExtra ingrediente = ingredienteRepository.findById(request.getIngredienteExtraId())
                .orElseThrow(() -> new EntityNotFoundException("Ingrediente extra no encontrado."));

        // Regla de negocio:
        // Un ingrediente no puede agregarse dos veces al mismo producto.
        if (repository.findByProductoIdAndIngredienteExtraId(
                producto.getId(),
                ingrediente.getId()).isPresent()) {

            throw new IllegalArgumentException("Este ingrediente ya está asociado al producto.");
        }

        ProductoIngredienteExtra relacion = new ProductoIngredienteExtra();

        relacion.setProducto(producto);
        relacion.setIngredienteExtra(ingrediente);

        ProductoIngredienteExtra saved = repository.save(relacion);

        return toResponse(saved);
    }

    // Entity -> DTO
    private ProductoIngredienteExtraResponse toResponse(ProductoIngredienteExtra relacion) {

        ProductoIngredienteExtraResponse response = new ProductoIngredienteExtraResponse();

        response.setId(relacion.getId());

        if (relacion.getProducto() != null) {
            response.setProducto(relacion.getProducto().getNombre());
        }

        if (relacion.getIngredienteExtra() != null) {
            response.setIngredienteExtra(relacion.getIngredienteExtra().getNombre());
        }

        return response;
    }
}