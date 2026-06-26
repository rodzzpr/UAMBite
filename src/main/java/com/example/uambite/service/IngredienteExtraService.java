package com.example.uambite.service;

import com.example.uambite.dto.request.IngredienteExtraRequest;
import com.example.uambite.dto.response.IngredienteExtraResponse;
import com.example.uambite.exceptions.ResourceNotFoundException;
import com.example.uambite.model.IngredienteExtra;
import com.example.uambite.repository.IngredienteExtraRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class IngredienteExtraService {

    private final IngredienteExtraRepository repository;

    @Transactional(readOnly = true)
    public List<IngredienteExtraResponse> getAll() {
        return repository.findAll().stream().map(this::toResponse).toList();
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

    private IngredienteExtra findOrThrow(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ingrediente extra no encontrado."));
    }

    private IngredienteExtraResponse toResponse(IngredienteExtra i) {
        return IngredienteExtraResponse.builder()
                .id(i.getId())
                .nombre(i.getNombre())
                .precioExtra(i.getPrecioExtra())
                .build();
    }
}
