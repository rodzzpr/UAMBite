package com.example.uambite.service;

import com.example.uambite.dto.request.PedidoRequest;
import com.example.uambite.dto.response.PedidoResponse;
import com.example.uambite.model.*;
import com.example.uambite.repository.*;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class PedidoService {

    private static final List<EstadoPedido> ESTADOS_ACTIVOS = Arrays.asList(
            EstadoPedido.PENDIENTE,
            EstadoPedido.PREPARANDO,
            EstadoPedido.EN_CAMINO
    );

    private final PedidoRepository repository;
    private final UsuarioRepository usuarioRepository;
    private final FranjaHorariaRepository franjaRepository;
    private final DescuentoRepository descuentoRepository;

    public PedidoService(PedidoRepository repository,
                         UsuarioRepository usuarioRepository,
                         FranjaHorariaRepository franjaRepository,
                         DescuentoRepository descuentoRepository) {
        this.repository = repository;
        this.usuarioRepository = usuarioRepository;
        this.franjaRepository = franjaRepository;
        this.descuentoRepository = descuentoRepository;
    }

    public List<PedidoResponse> getAll() {
        return repository.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public PedidoResponse getById(UUID id) {
        Pedido pedido = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Pedido no encontrado."));
        return toResponse(pedido);
    }

    @Transactional
    public PedidoResponse save(PedidoRequest request) {

        Usuario usuario = usuarioRepository.findById(request.getUsuarioId())
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado."));

        List<Pedido> pedidosActivos = repository.findByUsuarioIdAndEstadoIn(
                request.getUsuarioId(), ESTADOS_ACTIVOS);
        if (!pedidosActivos.isEmpty()) {
            throw new IllegalArgumentException("El usuario ya tiene un pedido activo.");
        }

        Pedido pedido = new Pedido();
        pedido.setTipoEntrega(request.getTipoEntrega());
        pedido.setUsuario(usuario);
        pedido.setEstado(EstadoPedido.PENDIENTE);
        pedido.setTotal(0.0);

        if (request.getFranjaHorariaId() != null) {
            FranjaHoraria franja = franjaRepository.findById(request.getFranjaHorariaId())
                    .orElseThrow(() -> new EntityNotFoundException("Franja horaria no encontrada."));
            if (!franja.getDisponible()) {
                throw new IllegalArgumentException("La franja horaria no está disponible.");
            }
            if (franja.getPedidosActuales() >= franja.getCapacidadMaxima()) {
                throw new IllegalArgumentException("La franja horaria está llena.");
            }
            franja.setPedidosActuales(franja.getPedidosActuales() + 1);
            franjaRepository.save(franja);
            pedido.setFranjaHoraria(franja);
        }

        if (request.getDescuentoId() != null) {
            Descuento descuento = descuentoRepository.findById(request.getDescuentoId())
                    .orElseThrow(() -> new EntityNotFoundException("Descuento no encontrado."));
            if (!descuento.getActivo()) {
                throw new IllegalArgumentException("El descuento no está activo.");
            }
            if (descuento.getFechaVencimiento().isBefore(java.time.LocalDate.now())) {
                throw new IllegalArgumentException("El descuento ha vencido.");
            }
            pedido.setDescuento(descuento);
        }

        Pedido saved = repository.save(pedido);
        return toResponse(saved);
    }

    @Transactional
    public PedidoResponse update(UUID id, PedidoRequest request) {

        Pedido pedido = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Pedido no encontrado."));

        if (pedido.getEstado() != EstadoPedido.PENDIENTE) {
            throw new IllegalArgumentException("Solo se pueden modificar pedidos pendientes.");
        }

        if (request.getTipoEntrega() != null) {
            pedido.setTipoEntrega(request.getTipoEntrega());
        }

        Pedido saved = repository.save(pedido);
        return toResponse(saved);
    }

    @Transactional
    public void delete(UUID id) {
        Pedido pedido = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Pedido no encontrado."));
        if (pedido.getEstado() != EstadoPedido.PENDIENTE
                && pedido.getEstado() != EstadoPedido.CANCELADO) {
            throw new IllegalArgumentException("Solo se pueden eliminar pedidos pendientes o cancelados.");
        }
        repository.delete(pedido);
    }

    @Transactional
    public PedidoResponse cancelarPedido(UUID id) {
        Pedido pedido = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Pedido no encontrado."));
        if (pedido.getEstado() == EstadoPedido.ENTREGADO
                || pedido.getEstado() == EstadoPedido.CANCELADO) {
            throw new IllegalArgumentException("El pedido no se puede cancelar en su estado actual.");
        }
        if (pedido.getFranjaHoraria() != null) {
            FranjaHoraria franja = pedido.getFranjaHoraria();
            franja.setPedidosActuales(franja.getPedidosActuales() - 1);
            franjaRepository.save(franja);
        }
        pedido.setEstado(EstadoPedido.CANCELADO);
        Pedido saved = repository.save(pedido);
        return toResponse(saved);
    }

    private PedidoResponse toResponse(Pedido pedido) {
        PedidoResponse response = new PedidoResponse();
        response.setId(pedido.getId());
        response.setEstado(pedido.getEstado());
        response.setTotal(pedido.getTotal());
        response.setTipoEntrega(pedido.getTipoEntrega());
        if (pedido.getUsuario() != null) {
            response.setUsuario(pedido.getUsuario().getNombre());
        }
        return response;
    }
}
