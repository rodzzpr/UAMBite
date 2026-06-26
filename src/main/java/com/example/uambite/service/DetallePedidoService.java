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

    // Guardar detalle
    public DetallePedidoResponse save(DetallePedidoRequest request) {

        Pedido pedido = pedidoRepository.findById(request.getPedidoId())
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));

        Producto producto = productoRepository.findById(request.getProductoId())
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        DetallePedido detalle = new DetallePedido();

        detalle.setCantidad(request.getCantidad());
        detalle.setPrecioUnitario(request.getPrecioUnitario());
        detalle.setSubtotal(request.getSubtotal());

        detalle.setPedido(pedido);
        detalle.setProducto(producto);

        DetallePedido saved = repository.save(detalle);

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