package com.example.uambite.service;

import com.example.uambite.dto.request.DetallePedidoRequest;
import com.example.uambite.dto.response.DetallePedidoIngredienteExtraResponse;
import com.example.uambite.dto.response.DetallePedidoResponse;
import com.example.uambite.exceptions.BusinessException;
import com.example.uambite.exceptions.ConflictException;
import com.example.uambite.exceptions.ResourceNotFoundException;
import com.example.uambite.model.*;
import com.example.uambite.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DetallePedidoService {

    private final DetallePedidoRepository repository;
    private final PedidoRepository pedidoRepository;
    private final ProductoRepository productoRepository;
    private final IngredienteExtraRepository ingredienteExtraRepository;
    private final DetallePedidoIngredienteExtraRepository detalleExtraRepository;
    private final PedidoService pedidoService;

    @Transactional(readOnly = true)
    public List<DetallePedidoResponse> getAll() {
        return repository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional
    public DetallePedidoResponse save(DetallePedidoRequest request) {
        Pedido pedido = pedidoRepository.findById(request.getPedidoId())
                .orElseThrow(() -> new ResourceNotFoundException("Pedido no encontrado."));

        Producto producto = productoRepository.findById(request.getProductoId())
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado."));

        if (pedido.getEstado() != EstadoPedido.PENDIENTE) {
            throw new BusinessException("El pedido ya no puede modificarse (estado: "
                    + pedido.getEstado() + ").");
        }

        if (producto.getStock() < request.getCantidad()) {
            throw new ConflictException("Stock insuficiente para el producto seleccionado.");
        }

        boolean tieneExtras = request.getIngredientesExtraIds() != null
                && !request.getIngredientesExtraIds().isEmpty();

        if (tieneExtras && !Boolean.TRUE.equals(producto.getPermitePersonalizacion())) {
            throw new BusinessException(
                    "El producto no permite personalización con ingredientes extra.");
        }

        DetallePedido detalle = DetallePedido.builder()
                .cantidad(request.getCantidad())
                .pedido(pedido)
                .producto(producto)
                .precioUnitario(producto.getPrecio())
                .subtotal(producto.getPrecio() * request.getCantidad())
                .ingredientesExtra(new ArrayList<>())
                .build();

        if (tieneExtras) {
            double extrasTotal = cargarExtras(detalle, request.getIngredientesExtraIds());
            detalle.setSubtotal(detalle.getSubtotal() + extrasTotal);
        }

        DetallePedido saved = repository.save(detalle);

        if (pedido.getDetalles() == null) {
            pedido.setDetalles(new ArrayList<>());
        }
        pedido.getDetalles().add(saved);
        pedidoService.recalcularTotal(pedido);
        pedidoRepository.save(pedido);

        return toResponse(saved);
    }

    @Transactional
    public void delete(UUID id) {
        DetallePedido detalle = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Detalle de pedido no encontrado."));

        Pedido pedido = detalle.getPedido();
        if (pedido == null
                || pedido.getEstado() != EstadoPedido.PENDIENTE) {
            throw new BusinessException("Solo se pueden eliminar detalles de pedidos PENDIENTES.");
        }

        if (pedido.getDetalles() != null) {
            pedido.getDetalles().remove(detalle);
            pedidoService.recalcularTotal(pedido);
            pedidoRepository.save(pedido);
        }
    }

    private double cargarExtras(DetallePedido detalle, List<UUID> ingredienteIds) {
        double total = 0.0;
        for (UUID id : ingredienteIds) {
            IngredienteExtra ing = ingredienteExtraRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Ingrediente extra no encontrado: " + id));
            DetallePedidoIngredienteExtra rel = DetallePedidoIngredienteExtra.builder()
                    .detallePedido(detalle)
                    .ingredienteExtra(ing)
                    .precioAdicional(ing.getPrecioExtra())
                    .build();
            detalle.getIngredientesExtra().add(rel);
            total += ing.getPrecioExtra();
        }
        return total;
    }

    private DetallePedidoResponse toResponse(DetallePedido detalle) {
        List<DetallePedidoIngredienteExtraResponse> extras = detalle.getIngredientesExtra() == null
                ? List.of()
                : detalle.getIngredientesExtra().stream()
                        .map(e -> DetallePedidoIngredienteExtraResponse.builder()
                                .id(e.getId())
                                .ingredienteExtraId(e.getIngredienteExtra().getId())
                                .nombre(e.getIngredienteExtra().getNombre())
                                .precioAdicional(e.getPrecioAdicional())
                                .build())
                        .collect(Collectors.toList());

        return DetallePedidoResponse.builder()
                .id(detalle.getId())
                .cantidad(detalle.getCantidad())
                .precioUnitario(detalle.getPrecioUnitario())
                .subtotal(detalle.getSubtotal())
                .producto(detalle.getProducto() != null ? detalle.getProducto().getNombre() : null)
                .productoId(detalle.getProducto() != null ? detalle.getProducto().getId() : null)
                .ingredientesExtra(extras)
                .build();
    }
}
