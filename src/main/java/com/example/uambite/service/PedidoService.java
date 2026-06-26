package com.example.uambite.service;

import com.example.uambite.dto.request.PedidoRequest;
import com.example.uambite.dto.response.DetallePedidoIngredienteExtraResponse;
import com.example.uambite.dto.response.DetallePedidoResponse;
import com.example.uambite.dto.response.PagoResponse;
import com.example.uambite.dto.response.PedidoResponse;
import com.example.uambite.exceptions.BusinessException;
import com.example.uambite.exceptions.ConflictException;
import com.example.uambite.exceptions.InvalidStateException;
import com.example.uambite.exceptions.ResourceNotFoundException;
import com.example.uambite.model.*;
import com.example.uambite.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PedidoService {

    private static final List<EstadoPedido> ESTADOS_ACTIVOS = Arrays.asList(
            EstadoPedido.PENDIENTE,
            EstadoPedido.CONFIRMADO,
            EstadoPedido.EN_PREPARACION,
            EstadoPedido.LISTO
    );

    private static final Set<EstadoPedido> ESTADOS_CON_STOCK = Set.of(
            EstadoPedido.CONFIRMADO,
            EstadoPedido.EN_PREPARACION,
            EstadoPedido.LISTO,
            EstadoPedido.EN_CAMINO,
            EstadoPedido.ENTREGADO
    );

    private final PedidoRepository repository;
    private final UsuarioRepository usuarioRepository;
    private final FranjaHorariaRepository franjaRepository;
    private final DescuentoRepository descuentoRepository;

    @Transactional(readOnly = true)
    public List<PedidoResponse> getAll() {
        return repository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public PedidoResponse getById(UUID id) {
        return toResponse(findOrThrow(id));
    }

    @Transactional
    public PedidoResponse save(PedidoRequest request) {
        Usuario usuario = usuarioRepository.findById(request.getUsuarioId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado."));

        validarPedidoUnicoActivo(request.getUsuarioId());

        Pedido pedido = Pedido.builder()
                .tipoEntrega(request.getTipoEntrega())
                .usuario(usuario)
                .estado(EstadoPedido.PENDIENTE)
                .total(0.0)
                .build();

        if (request.getFranjaHorariaId() != null) {
            FranjaHoraria franja = franjaRepository.findById(request.getFranjaHorariaId())
                    .orElseThrow(() -> new ResourceNotFoundException("Franja horaria no encontrada."));
            reservarFranja(franja);
            pedido.setFranjaHoraria(franja);
        }

        if (request.getDescuentoId() != null) {
            Descuento descuento = descuentoRepository.findById(request.getDescuentoId())
                    .orElseThrow(() -> new ResourceNotFoundException("Descuento no encontrado."));
            validarDescuento(descuento);
            pedido.setDescuento(descuento);
        }

        return toResponse(repository.save(pedido));
    }

    @Transactional
    public PedidoResponse update(UUID id, PedidoRequest request) {
        Pedido pedido = findOrThrow(id);
        if (pedido.getEstado() != EstadoPedido.PENDIENTE) {
            throw new InvalidStateException("Solo se pueden modificar pedidos en estado PENDIENTE.");
        }
        if (request.getTipoEntrega() != null) {
            pedido.setTipoEntrega(request.getTipoEntrega());
        }
        return toResponse(repository.save(pedido));
    }

    @Transactional
    public void delete(UUID id) {
        Pedido pedido = findOrThrow(id);
        if (pedido.getEstado() != EstadoPedido.PENDIENTE
                && pedido.getEstado() != EstadoPedido.CANCELADO) {
            throw new InvalidStateException(
                    "Solo se pueden eliminar pedidos en estado PENDIENTE o CANCELADO.");
        }
        if (pedido.getFranjaHoraria() != null) {
            liberarFranja(pedido.getFranjaHoraria());
        }
        repository.delete(pedido);
    }

    @Transactional
    public PedidoResponse cancelarPedido(UUID id) {
        Pedido pedido = findOrThrow(id);

        if (!EstadoPedidoTransiciones.puedeSerCancelado(pedido.getEstado())) {
            throw new InvalidStateException(
                    "El pedido no se puede cancelar en su estado actual: " + pedido.getEstado());
        }

        if (ESTADOS_CON_STOCK.contains(pedido.getEstado())) {
            restaurarStock(pedido);
        }

        if (pedido.getFranjaHoraria() != null) {
            liberarFranja(pedido.getFranjaHoraria());
        }

        EstadoPedidoTransiciones.validar(pedido.getEstado(), EstadoPedido.CANCELADO);
        pedido.setEstado(EstadoPedido.CANCELADO);
        return toResponse(repository.save(pedido));
    }

    @Transactional
    public PedidoResponse confirmarPedido(UUID id) {
        Pedido pedido = findOrThrow(id);
        EstadoPedidoTransiciones.validar(pedido.getEstado(), EstadoPedido.CONFIRMADO);
        decrementarStock(pedido);
        pedido.setEstado(EstadoPedido.CONFIRMADO);
        return toResponse(repository.save(pedido));
    }

    @Transactional
    public PedidoResponse marcarEnPreparacion(UUID id) {
        return transicionar(id, EstadoPedido.EN_PREPARACION);
    }

    @Transactional
    public PedidoResponse marcarListo(UUID id) {
        return transicionar(id, EstadoPedido.LISTO);
    }

    @Transactional(readOnly = true)
    public List<PedidoResponse> getMios(UUID usuarioId) {
        return repository.findByUsuarioId(usuarioId).stream()
                .map(this::toResponse).toList();
    }

    @Transactional
    public Pedido recalcularTotal(Pedido pedido) {
        List<DetallePedido> detalles = pedido.getDetalles() == null ? List.of() : pedido.getDetalles();
        double subtotal = detalles.stream()
                .mapToDouble(d -> d.getSubtotal() == null ? 0.0 : d.getSubtotal())
                .sum();
        double descuentoAplicado = 0.0;
        if (pedido.getDescuento() != null) {
            descuentoAplicado = subtotal * (pedido.getDescuento().getPorcentaje() / 100.0);
        }
        double total = redondear(subtotal - descuentoAplicado);
        pedido.setTotal(Math.max(total, 0.0));
        return pedido;
    }

    private PedidoResponse transicionar(UUID id, EstadoPedido destino) {
        Pedido pedido = findOrThrow(id);
        EstadoPedidoTransiciones.validar(pedido.getEstado(), destino);
        pedido.setEstado(destino);
        return toResponse(repository.save(pedido));
    }

    private void validarPedidoUnicoActivo(UUID usuarioId) {
        List<Pedido> activos = repository.findByUsuarioIdAndEstadoIn(usuarioId, ESTADOS_ACTIVOS);
        if (!activos.isEmpty()) {
            throw new ConflictException("El usuario ya tiene un pedido activo.");
        }
    }

    private void validarDescuento(Descuento descuento) {
        if (!Boolean.TRUE.equals(descuento.getActivo())) {
            throw new BusinessException("El descuento no está activo.");
        }
        if (descuento.getFechaVencimiento() != null
                && descuento.getFechaVencimiento().isBefore(LocalDate.now())) {
            throw new BusinessException("El descuento ha vencido.");
        }
    }

    private void reservarFranja(FranjaHoraria franja) {
        if (!Boolean.TRUE.equals(franja.getDisponible())) {
            throw new BusinessException("La franja horaria no está disponible.");
        }
        if (franja.getPedidosActuales() == null) {
            franja.setPedidosActuales(0);
        }
        if (franja.getPedidosActuales() >= franja.getCapacidadMaxima()) {
            throw new ConflictException("La franja horaria está llena.");
        }
        franja.setPedidosActuales(franja.getPedidosActuales() + 1);
        if (franja.getPedidosActuales().equals(franja.getCapacidadMaxima())) {
            franja.setDisponible(false);
        }
        franjaRepository.save(franja);
    }

    private void liberarFranja(FranjaHoraria franja) {
        if (franja.getPedidosActuales() == null || franja.getPedidosActuales() <= 0) {
            return;
        }
        franja.setPedidosActuales(franja.getPedidosActuales() - 1);
        if (franja.getPedidosActuales() < franja.getCapacidadMaxima()) {
            franja.setDisponible(true);
        }
        franjaRepository.save(franja);
    }

    private void decrementarStock(Pedido pedido) {
        if (pedido.getDetalles() == null) return;
        for (DetallePedido d : pedido.getDetalles()) {
            Producto p = d.getProducto();
            if (p == null) continue;
            if (p.getStock() < d.getCantidad()) {
                throw new ConflictException(
                        "Stock insuficiente para el producto: " + p.getNombre());
            }
            p.setStock(p.getStock() - d.getCantidad());
        }
    }

    private void restaurarStock(Pedido pedido) {
        if (pedido.getDetalles() == null) return;
        for (DetallePedido d : pedido.getDetalles()) {
            Producto p = d.getProducto();
            if (p == null) continue;
            p.setStock(p.getStock() + d.getCantidad());
        }
    }

    private double redondear(double v) {
        return BigDecimal.valueOf(v).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }

    private Pedido findOrThrow(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido no encontrado."));
    }

    private PedidoResponse toResponse(Pedido pedido) {
        recalcularTotal(pedido);

        List<DetallePedidoResponse> detallesResp = null;
        if (pedido.getDetalles() != null) {
            detallesResp = pedido.getDetalles().stream().map(d -> {
                List<DetallePedidoIngredienteExtraResponse> extras =
                        d.getIngredientesExtra() == null ? List.of() :
                        d.getIngredientesExtra().stream()
                                .map(e -> DetallePedidoIngredienteExtraResponse.builder()
                                        .id(e.getId())
                                        .ingredienteExtraId(e.getIngredienteExtra().getId())
                                        .nombre(e.getIngredienteExtra().getNombre())
                                        .precioAdicional(e.getPrecioAdicional())
                                        .build())
                                .toList();
                return DetallePedidoResponse.builder()
                        .id(d.getId())
                        .cantidad(d.getCantidad())
                        .precioUnitario(d.getPrecioUnitario())
                        .subtotal(d.getSubtotal())
                        .producto(d.getProducto() != null ? d.getProducto().getNombre() : null)
                        .productoId(d.getProducto() != null ? d.getProducto().getId() : null)
                        .ingredientesExtra(extras)
                        .build();
            }).toList();
        }

        PagoResponse pagoResp = null;
        if (pedido.getPago() != null) {
            Pago pago = pedido.getPago();
            pagoResp = PagoResponse.builder()
                    .id(pago.getId())
                    .metodoPago(pago.getMetodoPago())
                    .monto(pago.getMonto())
                    .fecha(pago.getFecha())
                    .estado(pago.getEstado())
                    .pedidoId(pedido.getId())
                    .build();
        }

        com.example.uambite.dto.response.EntregaResponse entregaResp = null;
        if (pedido.getEntrega() != null) {
            Entrega e = pedido.getEntrega();
            entregaResp = com.example.uambite.dto.response.EntregaResponse.builder()
                    .id(e.getId())
                    .ubicacion(e.getUbicacion())
                    .fechaEntrega(e.getFechaEntrega())
                    .estado(e.getEstado())
                    .pedidoId(pedido.getId())
                    .build();
        }

        return PedidoResponse.builder()
                .id(pedido.getId())
                .estado(pedido.getEstado())
                .total(pedido.getTotal())
                .subtotal(pedido.getDetalles() == null ? 0.0 :
                        pedido.getDetalles().stream()
                                .mapToDouble(d -> d.getSubtotal() == null ? 0.0 : d.getSubtotal())
                                .sum())
                .descuentoAplicado(pedido.getDescuento() != null && pedido.getDetalles() != null
                        ? pedido.getDetalles().stream()
                            .mapToDouble(d -> d.getSubtotal() == null ? 0.0 : d.getSubtotal()).sum()
                            * (pedido.getDescuento().getPorcentaje() / 100.0)
                        : 0.0)
                .tipoEntrega(pedido.getTipoEntrega())
                .usuario(pedido.getUsuario() != null ? pedido.getUsuario().getNombre() : null)
                .usuarioId(pedido.getUsuario() != null ? pedido.getUsuario().getId() : null)
                .franjaHorariaId(pedido.getFranjaHoraria() != null
                        ? pedido.getFranjaHoraria().getId() : null)
                .descuentoId(pedido.getDescuento() != null
                        ? pedido.getDescuento().getId() : null)
                .detalles(detallesResp)
                .pago(pagoResp)
                .entrega(entregaResp)
                .createdAt(pedido.getCreatedAt())
                .build();
    }
}
