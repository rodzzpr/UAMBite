package com.example.uambite.service;

import com.example.uambite.dto.request.LocalComidaRequest;
import com.example.uambite.dto.response.LocalComidaResponse;
import com.example.uambite.model.LocalComida;
import com.example.uambite.repository.LocalComidaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class LocalComidaService {

    private final LocalComidaRepository repository;

    public LocalComidaService(LocalComidaRepository repository) {
        this.repository = repository;
    }

    public List<LocalComidaResponse> getAll() {
        return repository.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public LocalComidaResponse save(LocalComidaRequest request) {

        LocalComida local = new LocalComida();

        local.setNombre(request.getNombre());
        local.setUbicacion(request.getUbicacion());
        local.setHorario(request.getHorario());
        local.setDisponible(request.getDisponible());

        LocalComida saved = repository.save(local);

        return toResponse(saved);
    }

    private LocalComidaResponse toResponse(LocalComida local) {

        LocalComidaResponse response = new LocalComidaResponse();

        response.setId(local.getId());
        response.setNombre(local.getNombre());
        response.setUbicacion(local.getUbicacion());
        response.setHorario(local.getHorario());
        response.setDisponible(local.getDisponible());

        return response;
    }
}
