package com.example.uambite.service;

import com.example.uambite.dto.request.DetallePedidoRequest;
import com.example.uambite.dto.response.DetallePedidoResponse;
import com.example.uambite.model.*;
import com.example.uambite.repository.DetallePedidoRepository;
import com.example.uambite.repository.PedidoRepository;
import com.example.uambite.repository.ProductoRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DetallePedidoService {

    private final DetallePedidoRepository repository;
    private final PedidoRepository pedidoRepository;
    private final ProductoRepository productoRepository;

    public DetallePedidoService(DetallePedidoRepository repository,
                                PedidoRepository pedidoRepository,
                                ProductoRepository productoRepository) {
        this.repository = repository;
        this.pedidoRepository = pedidoRepository;
        this.productoRepository = productoRepository;
    }

    public List<DetallePedidoResponse> getAll() {
        return repository.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public DetallePedidoResponse save(DetallePedidoRequest request) {

        Pedido pedido = pedidoRepository.findById(request.getPedidoId())
                .orElseThrow(() -> new EntityNotFoundException("Pedido no encontrado."));

        Producto producto = productoRepository.findById(request.getProductoId())
                .orElseThrow(() -> new EntityNotFoundException("Producto no encontrado."));

        if (pedido.getEstado() != EstadoPedido.PENDIENTE) {
            throw new IllegalArgumentException("El pedido ya no puede modificarse.");
        }

        if (producto.getStock() < request.getCantidad()) {
            throw new IllegalArgumentException("Stock insuficiente para el producto seleccionado.");
        }

        DetallePedido detalle = new DetallePedido();
        detalle.setCantidad(request.getCantidad());
        detalle.setPedido(pedido);
        detalle.setProducto(producto);
        detalle.setPrecioUnitario(producto.getPrecio());
        detalle.setSubtotal(producto.getPrecio() * request.getCantidad());

        DetallePedido saved = repository.save(detalle);

        List<DetallePedido> detalles = repository.findByPedidoId(pedido.getId());
        double total = detalles.stream().mapToDouble(DetallePedido::getSubtotal).sum();
        pedido.setTotal(total);
        pedidoRepository.save(pedido);

        producto.setStock(producto.getStock() - request.getCantidad());
        productoRepository.save(producto);

        return toResponse(saved);
    }

    private DetallePedidoResponse toResponse(DetallePedido detalle) {
        DetallePedidoResponse response = new DetallePedidoResponse();
        response.setId(detalle.getId());
        response.setCantidad(detalle.getCantidad());
        response.setPrecioUnitario(detalle.getPrecioUnitario());
        response.setSubtotal(detalle.getSubtotal());
        if (detalle.getProducto() != null) {
            response.setProducto(detalle.getProducto().getNombre());
        }
        return response;
    }
}
