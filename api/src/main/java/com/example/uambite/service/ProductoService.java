package com.example.uambite.service;

import com.example.uambite.dto.ImagenData;
import com.example.uambite.dto.request.ProductoRequest;
import com.example.uambite.dto.response.ProductoResponse;
import com.example.uambite.exceptions.BusinessException;
import com.example.uambite.exceptions.ResourceNotFoundException;
import com.example.uambite.model.LocalComida;
import com.example.uambite.model.Producto;
import com.example.uambite.repository.LocalComidaRepository;
import com.example.uambite.repository.ProductoRepository;
import com.example.uambite.util.ImageValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductoService {

    private final ProductoRepository repository;
    private final LocalComidaRepository localRepository;

    @Transactional(readOnly = true)
    public Page<ProductoResponse> getAll(UUID localComidaId, String nombre,
                                         BigDecimal maxPrecio, Pageable pageable) {
        boolean hasLocal = localComidaId != null;
        boolean hasNombre = nombre != null && !nombre.isBlank();
        boolean hasPrecio = maxPrecio != null;

        Page<Producto> page;
        if (hasLocal && hasNombre && hasPrecio) {
            page = repository.findByLocalComidaIdAndNombreContainingIgnoreCaseAndPrecioLessThanEqual(
                    localComidaId, nombre, maxPrecio, pageable);
        } else if (hasLocal && hasNombre) {
            page = repository.findByLocalComidaIdAndNombreContainingIgnoreCase(
                    localComidaId, nombre, pageable);
        } else if (hasLocal && hasPrecio) {
            page = repository.findByLocalComidaIdAndPrecioLessThanEqual(
                    localComidaId, maxPrecio, pageable);
        } else if (hasNombre && hasPrecio) {
            page = repository.findByNombreContainingIgnoreCaseAndPrecioLessThanEqual(
                    nombre, maxPrecio, pageable);
        } else if (hasLocal) {
            page = repository.findByLocalComidaId(localComidaId, pageable);
        } else if (hasNombre) {
            page = repository.findByNombreContainingIgnoreCase(nombre, pageable);
        } else if (hasPrecio) {
            page = repository.findByPrecioLessThanEqual(maxPrecio, pageable);
        } else {
            page = repository.findAll(pageable);
        }
        return page.map(this::toResponse);
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

    @Transactional
    public void setImagen(UUID id, MultipartFile file) {
        ImageValidator.validar(file);
        Producto producto = findOrThrow(id);
        try {
            producto.setImagen(file.getBytes());
            producto.setImagenTipo(file.getContentType());
        } catch (IOException e) {
            throw new BusinessException("No se pudo leer la imagen",
                    HttpStatus.INTERNAL_SERVER_ERROR, "IMAGE_READ_FAILED");
        }
        repository.save(producto);
    }

    @Transactional(readOnly = true)
    public ImagenData getImagen(UUID id) {
        Producto producto = findOrThrow(id);
        if (producto.getImagen() == null || producto.getImagen().length == 0) {
            throw new ResourceNotFoundException("El producto no tiene imagen");
        }
        return new ImagenData(producto.getImagen(), producto.getImagenTipo());
    }

    @Transactional
    public void removeImagen(UUID id) {
        Producto producto = findOrThrow(id);
        producto.setImagen(null);
        producto.setImagenTipo(null);
        repository.save(producto);
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
                .tieneImagen(producto.getImagen() != null && producto.getImagen().length > 0)
                .build();
    }
}
