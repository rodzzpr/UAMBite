package com.example.uambite.service;

import com.example.uambite.dto.request.CambioEstadoEntregaRequest;
import com.example.uambite.dto.request.EntregaRequest;
import com.example.uambite.dto.response.EntregaResponse;
import com.example.uambite.exceptions.BusinessException;
import com.example.uambite.exceptions.ConflictException;
import com.example.uambite.exceptions.ResourceNotFoundException;
import com.example.uambite.model.*;
import com.example.uambite.repository.EntregaRepository;
import com.example.uambite.repository.LocalComidaRepository;
import com.example.uambite.repository.PedidoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EntregaService {

    private final EntregaRepository repository;
    private final PedidoRepository pedidoRepository;
    private final LocalComidaRepository localComidaRepository;

    @Transactional(readOnly = true)
    public List<EntregaResponse> getAll() {
        return repository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<EntregaResponse> findAllByLocalComidaIds(java.util.Collection<UUID> localComidaIds) {
        return repository.findByPedido_LocalComidaIdIn(localComidaIds).stream()
                .map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<EntregaResponse> getAllForLocalOwner(UUID duenoId) {
        List<UUID> localIds = localComidaRepository.findByDuenoId(duenoId).stream()
                .map(LocalComida::getId).toList();
        if (localIds.isEmpty()) {
            return List.of();
        }
        return findAllByLocalComidaIds(localIds);
    }

    @Transactional
    public EntregaResponse save(EntregaRequest request) {
        Pedido pedido = pedidoRepository.findById(request.getPedidoId())
                .orElseThrow(() -> new ResourceNotFoundException("Pedido no encontrado."));

        if (pedido.getEntrega() != null) {
            throw new ConflictException("Este pedido ya tiene una entrega registrada.");
        }

        if (pedido.getPago() == null
                || pedido.getPago().getEstado() != EstadoPago.PAGADO) {
            throw new BusinessException(
                    "Solo se puede crear una entrega para pedidos pagados.");
        }

        EstadoPedidoTransiciones.validar(pedido.getEstado(), EstadoPedido.EN_CAMINO);

        Entrega entrega = Entrega.builder()
                .ubicacion(request.getUbicacion())
                .pedido(pedido)
                .fechaEntrega(LocalDateTime.now())
                .estado(EstadoEntrega.EN_CAMINO)
                .build();

        Entrega saved = repository.save(entrega);
        pedido.setEstado(EstadoPedido.EN_CAMINO);
        pedidoRepository.save(pedido);
        return toResponse(saved);
    }

    @Transactional
    public EntregaResponse finalizarEntrega(UUID entregaId) {
        Entrega entrega = repository.findById(entregaId)
                .orElseThrow(() -> new ResourceNotFoundException("Entrega no encontrada."));

        if (entrega.getEstado() == EstadoEntrega.ENTREGADA) {
            throw new BusinessException("La entrega ya fue finalizada.");
        }

        Pedido pedido = entrega.getPedido();
        if (pedido == null) {
            throw new BusinessException("La entrega no tiene un pedido asociado.");
        }

        entrega.setEstado(EstadoEntrega.ENTREGADA);
        EstadoPedidoTransiciones.validar(pedido.getEstado(), EstadoPedido.ENTREGADO);
        pedido.setEstado(EstadoPedido.ENTREGADO);
        repository.save(entrega);
        pedidoRepository.save(pedido);
        return toResponse(entrega);
    }

    @Transactional
    public EntregaResponse cambiarEstado(UUID entregaId, CambioEstadoEntregaRequest request) {
        EstadoEntrega nuevoEstado = request.getEstado();
        if (nuevoEstado != EstadoEntrega.ENTREGADA) {
            throw new BusinessException(
                    "Solo se permite transicionar la entrega a ENTREGADA desde este endpoint.");
        }
        return finalizarEntrega(entregaId);
    }

    private EntregaResponse toResponse(Entrega entrega) {
        return EntregaResponse.builder()
                .id(entrega.getId())
                .ubicacion(entrega.getUbicacion())
                .fechaEntrega(entrega.getFechaEntrega())
                .estado(entrega.getEstado())
                .pedidoId(entrega.getPedido() != null ? entrega.getPedido().getId() : null)
                .build();
    }
}
