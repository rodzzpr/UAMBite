package com.example.uambite.service;

import com.example.uambite.dto.request.EntregaRequest;
import com.example.uambite.dto.response.EntregaResponse;
import com.example.uambite.model.*;
import com.example.uambite.repository.EntregaRepository;
import com.example.uambite.repository.PedidoRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class EntregaService {

    private final EntregaRepository repository;
    private final PedidoRepository pedidoRepository;

    public EntregaService(EntregaRepository repository,
                          PedidoRepository pedidoRepository) {
        this.repository = repository;
        this.pedidoRepository = pedidoRepository;
    }

    public List<EntregaResponse> getAll() {
        return repository.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public EntregaResponse save(EntregaRequest request) {

        Pedido pedido = pedidoRepository.findById(request.getPedidoId())
                .orElseThrow(() -> new EntityNotFoundException("Pedido no encontrado."));

        if (pedido.getEntrega() != null) {
            throw new IllegalArgumentException("Este pedido ya tiene una entrega registrada.");
        }

        if (pedido.getPago() == null
                || pedido.getPago().getEstado() != EstadoPago.PAGADO) {
            throw new IllegalArgumentException("Solo se puede crear una entrega para pedidos pagados.");
        }

        Entrega entrega = new Entrega();
        entrega.setUbicacion(request.getUbicacion());
        entrega.setPedido(pedido);
        entrega.setFechaEntrega(LocalDateTime.now());
        entrega.setEstado(EstadoEntrega.EN_CAMINO);

        Entrega saved = repository.save(entrega);

        pedido.setEstado(EstadoPedido.EN_CAMINO);
        pedidoRepository.save(pedido);

        return toResponse(saved);
    }

    @Transactional
    public EntregaResponse finalizarEntrega(UUID entregaId) {

        Entrega entrega = repository.findById(entregaId)
                .orElseThrow(() -> new EntityNotFoundException("Entrega no encontrada."));

        if (entrega.getEstado() == EstadoEntrega.ENTREGADA) {
            throw new IllegalArgumentException("La entrega ya fue finalizada.");
        }

        entrega.setEstado(EstadoEntrega.ENTREGADA);

        Pedido pedido = entrega.getPedido();
        pedido.setEstado(EstadoPedido.ENTREGADO);

        repository.save(entrega);
        pedidoRepository.save(pedido);

        return toResponse(entrega);
    }

    private EntregaResponse toResponse(Entrega entrega) {
        EntregaResponse response = new EntregaResponse();
        response.setId(entrega.getId());
        response.setUbicacion(entrega.getUbicacion());
        response.setFechaEntrega(entrega.getFechaEntrega());
        response.setEstado(entrega.getEstado());
        if (entrega.getPedido() != null) {
            response.setPedidoId(entrega.getPedido().getId());
        }
        return response;
    }
}
