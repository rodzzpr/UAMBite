package com.example.uambite.service;

import com.example.uambite.dto.request.PagoRequest;
import com.example.uambite.dto.response.PagoResponse;
import com.example.uambite.model.Pago;
import com.example.uambite.model.Pedido;
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

    // Obtener todos los pagos
    public List<PagoResponse> getAll() {
        return repository.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // Registrar un pago
    @Transactional
    public PagoResponse save(PagoRequest request) {

        Pedido pedido = pedidoRepository.findById(request.getPedidoId())
                .orElseThrow(() -> new EntityNotFoundException("Pedido no encontrado."));

        // Regla 1: Un pedido solo puede tener un pago
        if (pedido.getPago() != null) {
            throw new IllegalArgumentException("El pedido ya tiene un pago registrado.");
        }

        // Regla 2: El pedido debe tener productos
        if (pedido.getTotal() <= 0) {
            throw new IllegalArgumentException("No se puede pagar un pedido sin productos.");
        }

        // Regla 3: Solo se pueden pagar pedidos pendientes
        if (!"PENDIENTE".equals(pedido.getEstado())) {
            throw new IllegalArgumentException("Solo se pueden pagar pedidos pendientes.");
        }

        Pago pago = new Pago();

        // Datos enviados por el cliente
        pago.setMetodoPago(request.getMetodoPago());
        pago.setPedido(pedido);

        // Datos generados por el sistema
        pago.setMonto(pedido.getTotal());
        pago.setFecha(LocalDateTime.now());
        pago.setEstado("PAGADO");

        Pago saved = repository.save(pago);

        // Actualizar estado del pedido
        pedido.setEstado("PAGADO");
        pedidoRepository.save(pedido);

        return toResponse(saved);
    }

    // Entity -> DTO
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