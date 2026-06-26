package com.example.uambite.service;

import com.example.uambite.dto.request.FranjaHorariaRequest;
import com.example.uambite.dto.response.FranjaHorariaResponse;
import com.example.uambite.model.FranjaHoraria;
import com.example.uambite.model.LocalComida;
import com.example.uambite.repository.FranjaHorariaRepository;
import com.example.uambite.repository.LocalComidaRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FranjaHorariaService {

    private final FranjaHorariaRepository repository;
    private final LocalComidaRepository localComidaRepository;

    public FranjaHorariaService(FranjaHorariaRepository repository,
                                LocalComidaRepository localComidaRepository) {
        this.repository = repository;
        this.localComidaRepository = localComidaRepository;
    }

    public List<FranjaHorariaResponse> getAll() {
        return repository.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public List<FranjaHorariaResponse> getDisponibles() {
        return repository.findByDisponibleTrue()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public FranjaHorariaResponse save(FranjaHorariaRequest request) {

        LocalComida local = localComidaRepository.findById(request.getLocalComidaId())
                .orElseThrow(() -> new EntityNotFoundException("Local de comida no encontrado"));

        if (request.getHoraFin().isBefore(request.getHoraInicio())
                || request.getHoraFin().equals(request.getHoraInicio())) {
            throw new IllegalArgumentException("La hora final debe ser mayor a la inicial");
        }

        FranjaHoraria franja = new FranjaHoraria();
        franja.setHoraInicio(request.getHoraInicio());
        franja.setHoraFin(request.getHoraFin());
        franja.setCapacidadMaxima(request.getCapacidadMaxima());
        franja.setPedidosActuales(0);
        franja.setDisponible(true);
        franja.setLocalComida(local);

        FranjaHoraria saved = repository.save(franja);
        return toResponse(saved);
    }

    private FranjaHorariaResponse toResponse(FranjaHoraria franja) {
        FranjaHorariaResponse response = new FranjaHorariaResponse();
        response.setId(franja.getId());
        response.setHoraInicio(franja.getHoraInicio());
        response.setHoraFin(franja.getHoraFin());
        response.setCapacidadMaxima(franja.getCapacidadMaxima());
        response.setPedidosActuales(franja.getPedidosActuales());
        response.setDisponible(franja.getDisponible());
        if (franja.getLocalComida() != null) {
            response.setLocalComida(franja.getLocalComida().getNombre());
        }
        return response;
    }
}
