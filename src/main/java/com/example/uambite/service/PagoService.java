package com.example.uambite.service;

import com.example.uambite.dto.request.PagoRequest;
import com.example.uambite.dto.response.PagoResponse;
import com.example.uambite.model.*;
import com.example.uambite.repository.PagoRepository;
import com.example.uambite.repository.PedidoRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PagoService {

    private final PagoRepository repository;
    private final PedidoRepository pedidoRepository;

    public PagoService(PagoRepository repository,
                       PedidoRepository pedidoRepository) {
        this.repository = repository;
        this.pedidoRepository = pedidoRepository;
    }

    public List<PagoResponse> getAll() {
        return repository.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public PagoResponse save(PagoRequest request) {

        Pedido pedido = pedidoRepository.findById(request.getPedidoId())
                .orElseThrow(() -> new EntityNotFoundException("Pedido no encontrado."));

        if (pedido.getPago() != null) {
            throw new IllegalArgumentException("El pedido ya tiene un pago registrado.");
        }

        if (pedido.getTotal() <= 0) {
            throw new IllegalArgumentException("No se puede pagar un pedido sin productos.");
        }

        if (pedido.getEstado() != EstadoPedido.PENDIENTE) {
            throw new IllegalArgumentException("Solo se pueden pagar pedidos pendientes.");
        }

        Pago pago = new Pago();
        pago.setMetodoPago(request.getMetodoPago());
        pago.setPedido(pedido);
        pago.setMonto(pedido.getTotal());
        pago.setFecha(LocalDateTime.now());
        pago.setEstado(EstadoPago.PAGADO);

        Pago saved = repository.save(pago);

        return toResponse(saved);
    }

    private PagoResponse toResponse(Pago pago) {
        PagoResponse response = new PagoResponse();
        response.setId(pago.getId());
        response.setMetodoPago(pago.getMetodoPago());
        response.setMonto(pago.getMonto());
        response.setFecha(pago.getFecha());
        response.setEstado(pago.getEstado());
        if (pago.getPedido() != null) {
            response.setPedidoId(pago.getPedido().getId());
        }
        return response;
    }
}
