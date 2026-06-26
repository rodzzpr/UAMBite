package com.example.uambite.service;

import com.example.uambite.dto.request.ProductoIngredienteExtraRequest;
import com.example.uambite.dto.response.ProductoIngredienteExtraResponse;
import com.example.uambite.exceptions.ConflictException;
import com.example.uambite.exceptions.ResourceNotFoundException;
import com.example.uambite.model.IngredienteExtra;
import com.example.uambite.model.Producto;
import com.example.uambite.model.ProductoIngredienteExtra;
import com.example.uambite.repository.IngredienteExtraRepository;
import com.example.uambite.repository.ProductoIngredienteExtraRepository;
import com.example.uambite.repository.ProductoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductoIngredienteExtraService {

    private final ProductoIngredienteExtraRepository repository;
    private final ProductoRepository productoRepository;
    private final IngredienteExtraRepository ingredienteRepository;

    @Transactional(readOnly = true)
    public List<ProductoIngredienteExtraResponse> getAll() {
        return repository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional
    public ProductoIngredienteExtraResponse save(ProductoIngredienteExtraRequest request) {
        Producto producto = productoRepository.findById(request.getProductoId())
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado."));

        IngredienteExtra ingrediente = ingredienteRepository.findById(request.getIngredienteExtraId())
                .orElseThrow(() -> new ResourceNotFoundException("Ingrediente extra no encontrado."));

        if (repository.findByProductoIdAndIngredienteExtraId(producto.getId(), ingrediente.getId())
                .isPresent()) {
            throw new ConflictException("Este ingrediente ya está asociado al producto.");
        }

        ProductoIngredienteExtra relacion = ProductoIngredienteExtra.builder()
                .producto(producto)
                .ingredienteExtra(ingrediente)
                .build();

        return toResponse(repository.save(relacion));
    }

    @Transactional
    public void delete(java.util.UUID id) {
        ProductoIngredienteExtra relacion = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Relación no encontrada."));
        repository.delete(relacion);
    }

    private ProductoIngredienteExtraResponse toResponse(ProductoIngredienteExtra r) {
        return ProductoIngredienteExtraResponse.builder()
                .id(r.getId())
                .productoId(r.getProducto() != null ? r.getProducto().getId() : null)
                .producto(r.getProducto() != null ? r.getProducto().getNombre() : null)
                .ingredienteExtraId(r.getIngredienteExtra() != null
                        ? r.getIngredienteExtra().getId() : null)
                .ingredienteExtra(r.getIngredienteExtra() != null
                        ? r.getIngredienteExtra().getNombre() : null)
                .build();
    }
}
