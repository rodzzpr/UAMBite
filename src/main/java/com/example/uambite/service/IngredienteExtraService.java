package com.example.uambite.service;

import com.example.uambite.dto.request.IngredienteExtraRequest;
import com.example.uambite.dto.response.IngredienteExtraResponse;
import com.example.uambite.model.IngredienteExtra;
import com.example.uambite.repository.IngredienteExtraRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class IngredienteExtraService {

    private final IngredienteExtraRepository repository;

    public IngredienteExtraService(IngredienteExtraRepository repository) {
        this.repository = repository;
    }

    public List<IngredienteExtraResponse> getAll() {
        return repository.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public IngredienteExtraResponse save(IngredienteExtraRequest request) {

        IngredienteExtra ingrediente = new IngredienteExtra();

        ingrediente.setNombre(request.getNombre());
        ingrediente.setPrecioExtra(request.getPrecioExtra());

        IngredienteExtra saved = repository.save(ingrediente);

        return toResponse(saved);
    }

    private IngredienteExtraResponse toResponse(IngredienteExtra ingrediente) {

        IngredienteExtraResponse response = new IngredienteExtraResponse();

        response.setId(ingrediente.getId());
        response.setNombre(ingrediente.getNombre());
        response.setPrecioExtra(ingrediente.getPrecioExtra());

        return response;
    }
}
