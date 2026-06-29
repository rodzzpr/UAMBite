package com.example.uambite.service;

import com.example.uambite.dto.ImagenData;
import com.example.uambite.dto.request.IngredienteExtraRequest;
import com.example.uambite.dto.response.IngredienteExtraResponse;
import com.example.uambite.exceptions.BusinessException;
import com.example.uambite.exceptions.ResourceNotFoundException;
import com.example.uambite.model.IngredienteExtra;
import com.example.uambite.repository.IngredienteExtraRepository;
import com.example.uambite.util.ImageValidator;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import lombok.RequiredArgsConstructor;
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
public class IngredienteExtraService {

    private final IngredienteExtraRepository repository;

    @Transactional(readOnly = true)
    public Page<IngredienteExtraResponse> getAll(Pageable pageable) {
        return repository.findAll(pageable).map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public IngredienteExtraResponse getById(UUID id) {
        return toResponse(findOrThrow(id));
    }

    @Transactional
    public IngredienteExtraResponse save(IngredienteExtraRequest request) {
        IngredienteExtra ing = IngredienteExtra.builder()
                .nombre(request.getNombre())
                .precioExtra(request.getPrecioExtra())
                .build();
        return toResponse(repository.save(ing));
    }

    @Transactional
    public IngredienteExtraResponse update(UUID id, IngredienteExtraRequest request) {
        IngredienteExtra ing = findOrThrow(id);
        ing.setNombre(request.getNombre());
        ing.setPrecioExtra(request.getPrecioExtra());
        return toResponse(repository.save(ing));
    }

    @Transactional
    public void delete(UUID id) {
        IngredienteExtra ing = findOrThrow(id);
        repository.delete(ing);
    }

    @Transactional
    public void setImagen(UUID id, MultipartFile file) {
        ImageValidator.validar(file);
        IngredienteExtra ing = findOrThrow(id);
        try {
            ing.setImagen(file.getBytes());
            ing.setImagenTipo(file.getContentType());
        } catch (IOException e) {
            throw new BusinessException("No se pudo leer la imagen",
                    HttpStatus.INTERNAL_SERVER_ERROR, "IMAGE_READ_FAILED");
        }
        repository.save(ing);
    }

    @Transactional(readOnly = true)
    public ImagenData getImagen(UUID id) {
        IngredienteExtra ing = findOrThrow(id);
        if (ing.getImagen() == null || ing.getImagen().length == 0) {
            throw new ResourceNotFoundException("El ingrediente no tiene imagen");
        }
        return new ImagenData(ing.getImagen(), ing.getImagenTipo());
    }

    @Transactional
    public void removeImagen(UUID id) {
        IngredienteExtra ing = findOrThrow(id);
        ing.setImagen(null);
        ing.setImagenTipo(null);
        repository.save(ing);
    }

    private IngredienteExtra findOrThrow(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ingrediente extra no encontrado."));
    }

    private IngredienteExtraResponse toResponse(IngredienteExtra i) {
        return IngredienteExtraResponse.builder()
                .id(i.getId())
                .nombre(i.getNombre())
                .precioExtra(i.getPrecioExtra())
                .tieneImagen(i.getImagen() != null && i.getImagen().length > 0)
                .build();
    }
}
