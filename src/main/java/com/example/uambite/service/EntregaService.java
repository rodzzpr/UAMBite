package com.example.uambite.service;

import com.example.uambite.dto.request.EntregaRequest;
import com.example.uambite.dto.response.EntregaResponse;
import com.example.uambite.model.Entrega;
import com.example.uambite.model.Pedido;
import com.example.uambite.repository.EntregaRepository;
import com.example.uambite.repository.PedidoRepository;
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
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado."));

        // Regla 1
        if (pedido.getEntrega() != null) {
            throw new RuntimeException("Este pedido ya tiene una entrega registrada.");
        }

        // Regla 2
        if (!"PAGADO".equals(pedido.getEstado())) {
            throw new RuntimeException("Solo se puede crear una entrega para pedidos pagados.");
        }

        Entrega entrega = new Entrega();

        // Datos enviados por el cliente
        entrega.setUbicacion(request.getUbicacion());
        entrega.setPedido(pedido);

        // Datos generados por el sistema
        entrega.setFechaEntrega(LocalDateTime.now());
        entrega.setEstado("EN_CAMINO");

        Entrega saved = repository.save(entrega);

        // Actualizar estado del pedido
        pedido.setEstado("EN_CAMINO");
        pedidoRepository.save(pedido);

        return toResponse(saved);
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

    @Transactional
    public EntregaResponse finalizarEntrega(UUID entregaId) {

        Entrega entrega = repository.findById(entregaId)
                .orElseThrow(() -> new RuntimeException("Entrega no encontrada."));

        if ("ENTREGADA".equals(entrega.getEstado())) {
            throw new RuntimeException("La entrega ya fue finalizada.");
        }

        entrega.setEstado("ENTREGADA");

        Pedido pedido = entrega.getPedido();
        pedido.setEstado("ENTREGADO");

        repository.save(entrega);
        pedidoRepository.save(pedido);

        return toResponse(entrega);
    }
}