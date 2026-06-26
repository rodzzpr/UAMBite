package com.example.uambite.service;

import com.example.uambite.dto.request.DetallePedidoRequest;
import com.example.uambite.dto.response.DetallePedidoResponse;
import com.example.uambite.model.DetallePedido;
import com.example.uambite.model.Pedido;
import com.example.uambite.model.Producto;
import com.example.uambite.repository.DetallePedidoRepository;
import com.example.uambite.repository.PedidoRepository;
import com.example.uambite.repository.ProductoRepository;
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

    // Obtener todos los detalles
    public List<DetallePedidoResponse> getAll() {
        return repository.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // Agregar un producto al pedido
    @Transactional
    public DetallePedidoResponse save(DetallePedidoRequest request) {

        Pedido pedido = pedidoRepository.findById(request.getPedidoId())
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado."));

        Producto producto = productoRepository.findById(request.getProductoId())
                .orElseThrow(() -> new RuntimeException("Producto no encontrado."));

        // Regla 1: Solo se pueden modificar pedidos pendientes
        if (!"PENDIENTE".equals(pedido.getEstado())) {
            throw new RuntimeException("El pedido ya no puede modificarse.");
        }

        // Regla 2: Validar stock suficiente
        if (producto.getStock() < request.getCantidad()) {
            throw new RuntimeException("Stock insuficiente para el producto seleccionado.");
        }

        DetallePedido detalle = new DetallePedido();

        // Datos enviados por el cliente
        detalle.setCantidad(request.getCantidad());
        detalle.setPedido(pedido);
        detalle.setProducto(producto);

        // Datos calculados por el sistema
        detalle.setPrecioUnitario(producto.getPrecio());
        detalle.setSubtotal(producto.getPrecio() * request.getCantidad());

        // Guardar detalle
        DetallePedido saved = repository.save(detalle);

        // Recalcular total del pedido
        List<DetallePedido> detalles = repository.findByPedidoId(pedido.getId());

        double total = detalles.stream()
                .mapToDouble(DetallePedido::getSubtotal)
                .sum();

        pedido.setTotal(total);
        pedidoRepository.save(pedido);

        // Actualizar stock
        producto.setStock(producto.getStock() - request.getCantidad());
        productoRepository.save(producto);

        return toResponse(saved);
    }

    // Entity -> DTO
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