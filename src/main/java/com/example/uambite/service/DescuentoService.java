package com.example.uambite.service;

import com.example.uambite.dto.request.DescuentoRequest;
import com.example.uambite.dto.response.DescuentoResponse;
import com.example.uambite.model.Descuento;
import com.example.uambite.model.LocalComida;
import com.example.uambite.repository.DescuentoRepository;
import com.example.uambite.repository.LocalComidaRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DescuentoService {

    private final DescuentoRepository repository;
    private final LocalComidaRepository localComidaRepository;

    public DescuentoService(DescuentoRepository repository,
                            LocalComidaRepository localComidaRepository) {
        this.repository = repository;
        this.localComidaRepository = localComidaRepository;
    }

    public List<DescuentoResponse> getAll() {
        return repository.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public DescuentoResponse save(DescuentoRequest request) {

        LocalComida local = localComidaRepository.findById(request.getLocalComidaId())
                .orElseThrow(() -> new EntityNotFoundException("Local de comida no encontrado"));

        Descuento descuento = new Descuento();

        descuento.setCodigo(request.getCodigo());
        descuento.setPorcentaje(request.getPorcentaje());
        descuento.setActivo(true);
        descuento.setFechaVencimiento(request.getFechaVencimiento());
        descuento.setLocalComida(local);

        Descuento saved = repository.save(descuento);

        return toResponse(saved);
    }

    private DescuentoResponse toResponse(Descuento descuento) {

        DescuentoResponse response = new DescuentoResponse();

        response.setId(descuento.getId());
        response.setCodigo(descuento.getCodigo());
        response.setPorcentaje(descuento.getPorcentaje());
        response.setActivo(descuento.getActivo());
        response.setFechaVencimiento(descuento.getFechaVencimiento());

        if (descuento.getLocalComida() != null) {
            response.setLocalComida(descuento.getLocalComida().getNombre());
        }

        return response;
    }
}
