package com.example.uambite.service;

import com.example.uambite.dto.request.LocalComidaRequest;
import com.example.uambite.dto.response.LocalComidaResponse;
import com.example.uambite.exceptions.ResourceNotFoundException;
import com.example.uambite.model.LocalComida;
import com.example.uambite.repository.LocalComidaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LocalComidaService {

    private final LocalComidaRepository repository;

    @Transactional(readOnly = true)
    public List<LocalComidaResponse> getAll() {
        return repository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public LocalComidaResponse getById(UUID id) {
        return toResponse(findOrThrow(id));
    }

    @Transactional
    public LocalComidaResponse save(LocalComidaRequest request) {
        LocalComida local = LocalComida.builder()
                .nombre(request.getNombre())
                .ubicacion(request.getUbicacion())
                .horario(request.getHorario())
                .build();
        return toResponse(repository.save(local));
    }

    @Transactional
    public LocalComidaResponse update(UUID id, LocalComidaRequest request) {
        LocalComida local = findOrThrow(id);
        local.setNombre(request.getNombre());
        local.setUbicacion(request.getUbicacion());
        local.setHorario(request.getHorario());
        return toResponse(repository.save(local));
    }

    @Transactional
    public void delete(UUID id) {
        LocalComida local = findOrThrow(id);
        repository.delete(local);
    }

    private LocalComida findOrThrow(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Local de comida no encontrado."));
    }

    private LocalComidaResponse toResponse(LocalComida local) {
        return LocalComidaResponse.builder()
                .id(local.getId())
                .nombre(local.getNombre())
                .ubicacion(local.getUbicacion())
                .horario(local.getHorario())
                .build();
    }
}
