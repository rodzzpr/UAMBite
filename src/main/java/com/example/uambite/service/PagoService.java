package com.example.uambite.service;

import com.example.uambite.dto.request.PagoRequest;
import com.example.uambite.dto.response.PagoResponse;
import com.example.uambite.exceptions.BusinessException;
import com.example.uambite.exceptions.ConflictException;
import com.example.uambite.exceptions.ResourceNotFoundException;
import com.example.uambite.model.*;
import com.example.uambite.repository.PagoRepository;
import com.example.uambite.repository.PedidoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PagoService {

    private final PagoRepository repository;
    private final PedidoRepository pedidoRepository;

    @Transactional(readOnly = true)
    public List<PagoResponse> getAll() {
        return repository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional
    public PagoResponse save(PagoRequest request) {
        Pedido pedido = pedidoRepository.findById(request.getPedidoId())
                .orElseThrow(() -> new ResourceNotFoundException("Pedido no encontrado."));

        if (pedido.getPago() != null) {
            throw new ConflictException("El pedido ya tiene un pago registrado.");
        }

        if (pedido.getTotal() == null
                || pedido.getTotal().compareTo(java.math.BigDecimal.ZERO) <= 0) {
            throw new BusinessException("No se puede pagar un pedido sin productos.");
        }

        if (pedido.getEstado() != EstadoPedido.PENDIENTE) {
            throw new BusinessException("Solo se pueden pagar pedidos en estado PENDIENTE. Estado actual: "
                    + pedido.getEstado());
        }

        Pago pago = Pago.builder()
                .metodoPago(request.getMetodoPago())
                .pedido(pedido)
                .monto(pedido.getTotal())
                .fecha(LocalDateTime.now())
                .estado(EstadoPago.PAGADO)
                .build();

        return toResponse(repository.save(pago));
    }

    @Transactional(readOnly = true)
    public PagoResponse getById(UUID id) {
        return toResponse(repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pago no encontrado.")));
    }

    private PagoResponse toResponse(Pago pago) {
        return PagoResponse.builder()
                .id(pago.getId())
                .metodoPago(pago.getMetodoPago())
                .monto(pago.getMonto())
                .fecha(pago.getFecha())
                .estado(pago.getEstado())
                .pedidoId(pago.getPedido() != null ? pago.getPedido().getId() : null)
                .build();
    }
}
