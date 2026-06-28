package com.example.uambite.service;

import com.example.uambite.dto.request.DescuentoRequest;
import com.example.uambite.dto.response.DescuentoResponse;
import com.example.uambite.exceptions.ConflictException;
import com.example.uambite.exceptions.ResourceNotFoundException;
import com.example.uambite.model.Descuento;
import com.example.uambite.model.LocalComida;
import com.example.uambite.repository.DescuentoRepository;
import com.example.uambite.repository.LocalComidaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DescuentoService {

    private final DescuentoRepository repository;
    private final LocalComidaRepository localRepository;

    @Transactional(readOnly = true)
    public Page<DescuentoResponse> getAll(Pageable pageable) {
        return repository.findAll(pageable).map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public DescuentoResponse getById(UUID id) {
        return toResponse(findOrThrow(id));
    }

    @Transactional
    public DescuentoResponse save(DescuentoRequest request) {
        if (repository.findByCodigo(request.getCodigo()).isPresent()) {
            throw new ConflictException("Ya existe un descuento con ese código.");
        }

        LocalComida local = null;
        if (request.getLocalComidaId() != null) {
            local = localRepository.findById(request.getLocalComidaId())
                    .orElseThrow(() -> new ResourceNotFoundException("Local de comida no encontrado."));
        }

        Descuento descuento = Descuento.builder()
                .codigo(request.getCodigo())
                .porcentaje(request.getPorcentaje())
                .fechaVencimiento(request.getFechaVencimiento())
                .activo(request.getActivo())
                .localComida(local)
                .build();

        return toResponse(repository.save(descuento));
    }

    @Transactional
    public DescuentoResponse update(UUID id, DescuentoRequest request) {
        Descuento descuento = findOrThrow(id);
        descuento.setCodigo(request.getCodigo());
        descuento.setPorcentaje(request.getPorcentaje());
        descuento.setFechaVencimiento(request.getFechaVencimiento());
        descuento.setActivo(request.getActivo());
        if (request.getLocalComidaId() != null) {
            descuento.setLocalComida(localRepository.findById(request.getLocalComidaId())
                    .orElseThrow(() -> new ResourceNotFoundException("Local de comida no encontrado.")));
        }
        return toResponse(repository.save(descuento));
    }

    @Transactional
    public void delete(UUID id) {
        Descuento descuento = findOrThrow(id);
        repository.delete(descuento);
    }

    private Descuento findOrThrow(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Descuento no encontrado."));
    }

    private DescuentoResponse toResponse(Descuento d) {
        return DescuentoResponse.builder()
                .id(d.getId())
                .codigo(d.getCodigo())
                .porcentaje(d.getPorcentaje() == null ? BigDecimal.ZERO : d.getPorcentaje())
                .fechaVencimiento(d.getFechaVencimiento())
                .activo(d.getActivo())
                .localComida(d.getLocalComida() != null ? d.getLocalComida().getNombre() : null)
                .build();
    }
}
