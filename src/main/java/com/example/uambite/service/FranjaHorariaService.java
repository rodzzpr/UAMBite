package com.example.uambite.service;

import com.example.uambite.dto.request.FranjaHorariaRequest;
import com.example.uambite.dto.response.FranjaHorariaResponse;
import com.example.uambite.exceptions.BusinessException;
import com.example.uambite.exceptions.ResourceNotFoundException;
import com.example.uambite.model.FranjaHoraria;
import com.example.uambite.model.LocalComida;
import com.example.uambite.repository.FranjaHorariaRepository;
import com.example.uambite.repository.LocalComidaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FranjaHorariaService {

    private final FranjaHorariaRepository repository;
    private final LocalComidaRepository localRepository;

    @Transactional(readOnly = true)
    public Page<FranjaHorariaResponse> getAll(Pageable pageable) {
        return repository.findAll(pageable).map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public Page<FranjaHorariaResponse> getDisponibles(Pageable pageable) {
        return repository.findByDisponibleTrue(pageable).map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public FranjaHorariaResponse getById(UUID id) {
        return toResponse(findOrThrow(id));
    }

    @Transactional
    public FranjaHorariaResponse save(FranjaHorariaRequest request) {
        if (request.getHoraFin().isBefore(request.getHoraInicio())
                || request.getHoraFin().equals(request.getHoraInicio())) {
            throw new BusinessException("La hora de fin debe ser posterior a la hora de inicio.");
        }
        LocalComida local = localRepository.findById(request.getLocalComidaId())
                .orElseThrow(() -> new ResourceNotFoundException("Local de comida no encontrado."));

        FranjaHoraria franja = FranjaHoraria.builder()
                .horaInicio(request.getHoraInicio())
                .horaFin(request.getHoraFin())
                .capacidadMaxima(request.getCapacidadMaxima())
                .pedidosActuales(request.getPedidosActuales() == null
                        ? 0 : request.getPedidosActuales())
                .disponible(request.getDisponible() == null
                        ? Boolean.TRUE : request.getDisponible())
                .localComida(local)
                .build();

        return toResponse(repository.save(franja));
    }

    @Transactional
    public FranjaHorariaResponse update(UUID id, FranjaHorariaRequest request) {
        FranjaHoraria franja = findOrThrow(id);
        if (request.getHoraFin().isBefore(request.getHoraInicio())
                || request.getHoraFin().equals(request.getHoraInicio())) {
            throw new BusinessException("La hora de fin debe ser posterior a la hora de inicio.");
        }
        franja.setHoraInicio(request.getHoraInicio());
        franja.setHoraFin(request.getHoraFin());
        franja.setCapacidadMaxima(request.getCapacidadMaxima());
        if (request.getDisponible() != null) {
            franja.setDisponible(request.getDisponible());
        }
        return toResponse(repository.save(franja));
    }

    @Transactional
    public void delete(UUID id) {
        FranjaHoraria franja = findOrThrow(id);
        repository.delete(franja);
    }

    private FranjaHoraria findOrThrow(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Franja horaria no encontrada."));
    }

    private FranjaHorariaResponse toResponse(FranjaHoraria f) {
        return FranjaHorariaResponse.builder()
                .id(f.getId())
                .horaInicio(f.getHoraInicio())
                .horaFin(f.getHoraFin())
                .capacidadMaxima(f.getCapacidadMaxima())
                .pedidosActuales(f.getPedidosActuales())
                .disponible(f.getDisponible())
                .localComidaId(f.getLocalComida() != null ? f.getLocalComida().getId() : null)
                .localComida(f.getLocalComida() != null ? f.getLocalComida().getNombre() : null)
                .build();
    }
}
