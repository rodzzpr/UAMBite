package com.example.uambite.service;

import com.example.uambite.dto.request.CambioEstadoPedidoRequest;
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
import com.example.uambite.repository.ProductoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
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
    private final ProductoRepository productoRepository;
    private final LocalComidaRepository localComidaRepository;

    @Transactional(readOnly = true)
    public List<PedidoResponse> getAll() {
        return repository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<PedidoResponse> getAllForLocalOwner(UUID duenoId) {
        List<UUID> localIds = localComidaRepository.findByDuenoId(duenoId).stream()
                .map(LocalComida::getId).toList();
        if (localIds.isEmpty()) {
            return List.of();
        }
        return repository.findByLocalComidaIdInOrderByPrioridadDescCreatedAtAsc(localIds).stream()
                .map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public PedidoResponse getById(UUID id) {
        return toResponse(findOrThrow(id));
    }

    @Transactional
    public PedidoResponse save(PedidoRequest request) {
        UUID usuarioObjetivo = request.getUsuarioId();

        Usuario usuario = usuarioRepository.findById(usuarioObjetivo)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado."));

        validarPedidoUnicoActivo(usuarioObjetivo);

        Pedido pedido = Pedido.builder()
                .tipoEntrega(request.getTipoEntrega())
                .usuario(usuario)
                .estado(EstadoPedido.PENDIENTE)
                .total(BigDecimal.ZERO)
                .build();

        if (request.getFranjaHorariaId() != null) {
            FranjaHoraria franja = franjaRepository.findById(request.getFranjaHorariaId())
                    .orElseThrow(() -> new ResourceNotFoundException("Franja horaria no encontrada."));
            reservarFranja(franja);
            pedido.setFranjaHoraria(franja);
            if (franja.getLocalComida() != null) {
                pedido.setLocalComidaId(franja.getLocalComida().getId());
            }
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
                && pedido.getEstado() != EstadoPedido.CANCELADO
                && pedido.getEstado() != EstadoPedido.ENTREGADO) {
            throw new InvalidStateException(
                    "Solo se pueden eliminar pedidos en estado PENDIENTE, CANCELADO o ENTREGADO.");
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
        if (pedido.getDetalles() == null || pedido.getDetalles().isEmpty()) {
            throw new BusinessException(
                    "No se puede confirmar un pedido sin productos.");
        }
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
    public PedidoResponse setPrioridad(UUID id, Integer prioridad) {
        Pedido pedido = findOrThrow(id);
        if (prioridad == null || prioridad < 0) {
            throw new BusinessException("La prioridad debe ser un entero >= 0.");
        }
        pedido.setPrioridad(prioridad);
        return toResponse(repository.save(pedido));
    }

    @Transactional
    public PedidoResponse cambiarEstado(UUID id, CambioEstadoPedidoRequest request) {
        Pedido pedido = findOrThrow(id);
        EstadoPedido nuevoEstado = request.getEstado();
        EstadoPedidoTransiciones.validar(pedido.getEstado(), nuevoEstado);

        switch (nuevoEstado) {
            case CONFIRMADO:
                if (pedido.getDetalles() == null || pedido.getDetalles().isEmpty()) {
                    throw new BusinessException(
                            "No se puede confirmar un pedido sin productos.");
                }
                decrementarStock(pedido);
                break;
            case CANCELADO:
                if (ESTADOS_CON_STOCK.contains(pedido.getEstado())) {
                    restaurarStock(pedido);
                }
                if (pedido.getFranjaHoraria() != null) {
                    liberarFranja(pedido.getFranjaHoraria());
                }
                break;
            case EN_PREPARACION:
            case LISTO:
            case ENTREGADO:
                break;
            default:
                throw new BusinessException(
                        "La transición a " + nuevoEstado
                        + " debe realizarse a través de la gestión de entregas.");
        }

        pedido.setEstado(nuevoEstado);
        return toResponse(repository.save(pedido));
    }

    @Transactional
    public Pedido recalcularTotal(Pedido pedido) {
        List<DetallePedido> detalles = pedido.getDetalles() == null ? List.of() : pedido.getDetalles();
        BigDecimal subtotal = detalles.stream()
                .map(d -> d.getSubtotal() == null ? BigDecimal.ZERO : d.getSubtotal())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal descuentoAplicado = BigDecimal.ZERO;
        if (pedido.getDescuento() != null && pedido.getDescuento().getPorcentaje() != null) {
            BigDecimal porcentaje = pedido.getDescuento().getPorcentaje();
            descuentoAplicado = subtotal.multiply(porcentaje)
                    .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
        }

        BigDecimal total = subtotal.subtract(descuentoAplicado).setScale(2, RoundingMode.HALF_UP);
        pedido.setTotal(total.compareTo(BigDecimal.ZERO) < 0 ? BigDecimal.ZERO : total);
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
        FranjaHoraria locked = franjaRepository.findByIdWithLock(franja.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Franja horaria no encontrada."));
        if (!Boolean.TRUE.equals(locked.getDisponible())) {
            throw new BusinessException("La franja horaria no está disponible.");
        }
        if (locked.getPedidosActuales() == null) {
            locked.setPedidosActuales(0);
        }
        if (locked.getPedidosActuales() >= locked.getCapacidadMaxima()) {
            throw new ConflictException("La franja horaria está llena.");
        }
        locked.setPedidosActuales(locked.getPedidosActuales() + 1);
        if (locked.getPedidosActuales().equals(locked.getCapacidadMaxima())) {
            locked.setDisponible(false);
        }
        franjaRepository.save(locked);
    }

    private void liberarFranja(FranjaHoraria franja) {
        FranjaHoraria locked = franjaRepository.findByIdWithLock(franja.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Franja horaria no encontrada."));
        if (locked.getPedidosActuales() == null || locked.getPedidosActuales() <= 0) {
            return;
        }
        locked.setPedidosActuales(locked.getPedidosActuales() - 1);
        if (locked.getPedidosActuales() < locked.getCapacidadMaxima()) {
            locked.setDisponible(true);
        }
        franjaRepository.save(locked);
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
            productoRepository.save(p);
        }
    }

    private void restaurarStock(Pedido pedido) {
        if (pedido.getDetalles() == null) return;
        for (DetallePedido d : pedido.getDetalles()) {
            Producto p = d.getProducto();
            if (p == null) continue;
            p.setStock(p.getStock() + d.getCantidad());
            productoRepository.save(p);
        }
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
                                        .precioAdicional(e.getPrecioAdicional() == null
                                                ? BigDecimal.ZERO : e.getPrecioAdicional())
                                        .build())
                                .toList();
                return DetallePedidoResponse.builder()
                        .id(d.getId())
                        .cantidad(d.getCantidad())
                        .precioUnitario(d.getPrecioUnitario() == null
                                ? BigDecimal.ZERO : d.getPrecioUnitario())
                        .subtotal(d.getSubtotal() == null ? BigDecimal.ZERO : d.getSubtotal())
                        .producto(d.getProducto() != null ? d.getProducto().getNombre() : null)
                        .productoId(d.getProducto() != null ? d.getProducto().getId() : null)
                        .ingredientesExtra(extras)
                        .build();
            }).toList();
        }

        BigDecimal subtotal = pedido.getDetalles() == null ? BigDecimal.ZERO :
                pedido.getDetalles().stream()
                        .map(d -> d.getSubtotal() == null ? BigDecimal.ZERO : d.getSubtotal())
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal descuentoAplicado = BigDecimal.ZERO;
        if (pedido.getDescuento() != null && pedido.getDescuento().getPorcentaje() != null) {
            descuentoAplicado = subtotal.multiply(pedido.getDescuento().getPorcentaje())
                    .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
        }

        PagoResponse pagoResp = null;
        if (pedido.getPago() != null) {
            Pago pago = pedido.getPago();
            pagoResp = PagoResponse.builder()
                    .id(pago.getId())
                    .metodoPago(pago.getMetodoPago())
                    .monto(pago.getMonto() == null ? BigDecimal.ZERO : pago.getMonto())
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
                .total(pedido.getTotal() == null ? BigDecimal.ZERO : pedido.getTotal())
                .subtotal(subtotal)
                .descuentoAplicado(descuentoAplicado)
                .tipoEntrega(pedido.getTipoEntrega())
                .usuario(pedido.getUsuario() != null ? pedido.getUsuario().getNombre() : null)
                .usuarioId(pedido.getUsuario() != null ? pedido.getUsuario().getId() : null)
                .franjaHorariaId(pedido.getFranjaHoraria() != null
                        ? pedido.getFranjaHoraria().getId() : null)
                .descuentoId(pedido.getDescuento() != null
                        ? pedido.getDescuento().getId() : null)
                .localComidaId(pedido.getLocalComidaId())
                .prioridad(pedido.getPrioridad() == null ? 0 : pedido.getPrioridad())
                .detalles(detallesResp)
                .pago(pagoResp)
                .entrega(entregaResp)
                .createdAt(pedido.getCreatedAt())
                .build();
    }
}
