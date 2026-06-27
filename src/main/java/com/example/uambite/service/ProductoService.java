package com.example.uambite.service;

import com.example.uambite.dto.request.ProductoRequest;
import com.example.uambite.dto.response.ProductoResponse;
import com.example.uambite.exceptions.ResourceNotFoundException;
import com.example.uambite.model.LocalComida;
import com.example.uambite.model.Producto;
import com.example.uambite.repository.LocalComidaRepository;
import com.example.uambite.repository.ProductoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductoService {

    private final ProductoRepository repository;
    private final LocalComidaRepository localRepository;

    @Transactional(readOnly = true)
    public List<ProductoResponse> getAll() {
        return repository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public ProductoResponse getById(UUID id) {
        return toResponse(findOrThrow(id));
    }

    @Transactional
    public ProductoResponse save(ProductoRequest request) {
        LocalComida local = localRepository.findById(request.getLocalComidaId())
                .orElseThrow(() -> new ResourceNotFoundException("Local de comida no encontrado."));

        Producto producto = Producto.builder()
                .nombre(request.getNombre())
                .descripcion(request.getDescripcion())
                .precio(request.getPrecio())
                .stock(request.getStock())
                .permitePersonalizacion(request.getPermitePersonalizacion())
                .localComida(local)
                .build();

        return toResponse(repository.save(producto));
    }

    @Transactional
    public ProductoResponse update(UUID id, ProductoRequest request) {
        Producto producto = findOrThrow(id);
        LocalComida local = localRepository.findById(request.getLocalComidaId())
                .orElseThrow(() -> new ResourceNotFoundException("Local de comida no encontrado."));
        producto.setNombre(request.getNombre());
        producto.setDescripcion(request.getDescripcion());
        producto.setPrecio(request.getPrecio());
        producto.setStock(request.getStock());
        producto.setPermitePersonalizacion(request.getPermitePersonalizacion());
        producto.setLocalComida(local);
        return toResponse(repository.save(producto));
    }

    @Transactional
    public void delete(UUID id) {
        Producto producto = findOrThrow(id);
        repository.delete(producto);
    }

    private Producto findOrThrow(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado."));
    }

    private ProductoResponse toResponse(Producto producto) {
        return ProductoResponse.builder()
                .id(producto.getId())
                .nombre(producto.getNombre())
                .descripcion(producto.getDescripcion())
                .precio(producto.getPrecio())
                .stock(producto.getStock())
                .permitePersonalizacion(producto.getPermitePersonalizacion())
                .localComida(producto.getLocalComida() != null
                        ? producto.getLocalComida().getNombre() : null)
                .ingredientesExtraIds(producto.getIngredientesExtras() == null ? List.of() :
                        producto.getIngredientesExtras().stream()
                                .map(pi -> pi.getIngredienteExtra().getId()).toList())
                .build();
    }
}
