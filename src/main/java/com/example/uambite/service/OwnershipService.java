package com.example.uambite.service;

import com.example.uambite.model.LocalComida;
import com.example.uambite.repository.DescuentoRepository;
import com.example.uambite.repository.EntregaRepository;
import com.example.uambite.repository.FranjaHorariaRepository;
import com.example.uambite.repository.LocalComidaRepository;
import com.example.uambite.repository.PedidoRepository;
import com.example.uambite.repository.ProductoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service("ownershipService")
@RequiredArgsConstructor
public class OwnershipService {

    private final LocalComidaRepository localComidaRepository;
    private final ProductoRepository productoRepository;
    private final FranjaHorariaRepository franjaHorariaRepository;
    private final DescuentoRepository descuentoRepository;
    private final PedidoRepository pedidoRepository;
    private final EntregaRepository entregaRepository;

    @Transactional(readOnly = true)
    public boolean canEditLocal(UUID localId, UUID userId) {
        if (localId == null || userId == null) {
            return false;
        }
        return localComidaRepository.findById(localId)
                .map(l -> isOwnerOrNull(l, userId))
                .orElse(false);
    }

    @Transactional(readOnly = true)
    public boolean canViewLocal(UUID localId, UUID userId) {
        return canEditLocal(localId, userId);
    }

    @Transactional(readOnly = true)
    public boolean canEditProducto(UUID productoId, UUID userId) {
        if (productoId == null || userId == null) {
            return false;
        }
        return productoRepository.findById(productoId)
                .map(p -> p.getLocalComida() != null && isOwnerOrNull(p.getLocalComida(), userId))
                .orElse(false);
    }

    @Transactional(readOnly = true)
    public boolean canEditFranja(UUID franjaId, UUID userId) {
        if (franjaId == null || userId == null) {
            return false;
        }
        return franjaHorariaRepository.findById(franjaId)
                .map(f -> f.getLocalComida() != null && isOwnerOrNull(f.getLocalComida(), userId))
                .orElse(false);
    }

    @Transactional(readOnly = true)
    public boolean canEditDescuento(UUID descuentoId, UUID userId) {
        if (descuentoId == null || userId == null) {
            return false;
        }
        return descuentoRepository.findById(descuentoId)
                .map(d -> d.getLocalComida() != null && isOwnerOrNull(d.getLocalComida(), userId))
                .orElse(false);
    }

    @Transactional(readOnly = true)
    public boolean canManagePedido(UUID pedidoId, UUID userId) {
        if (pedidoId == null || userId == null) {
            return false;
        }
        return pedidoRepository.findById(pedidoId)
                .map(p -> p.getLocalComidaId() != null && canEditLocal(p.getLocalComidaId(), userId))
                .orElse(false);
    }

    @Transactional(readOnly = true)
    public boolean canViewPedido(UUID pedidoId, UUID userId) {
        if (pedidoId == null || userId == null) {
            return false;
        }
        return pedidoRepository.findById(pedidoId)
                .map(p -> {
                    if (p.getUsuario() != null && userId.equals(p.getUsuario().getId())) {
                        return true;
                    }
                    return p.getLocalComidaId() != null && canEditLocal(p.getLocalComidaId(), userId);
                })
                .orElse(false);
    }

    @Transactional(readOnly = true)
    public boolean canManageEntrega(UUID entregaId, UUID userId) {
        if (entregaId == null || userId == null) {
            return false;
        }
        return entregaRepository.findById(entregaId)
                .map(e -> e.getPedido() != null
                        && e.getPedido().getLocalComidaId() != null
                        && canEditLocal(e.getPedido().getLocalComidaId(), userId))
                .orElse(false);
    }

    @Transactional(readOnly = true)
    public boolean canEditLocalByProducto(UUID productoId, UUID userId) {
        if (productoId == null || userId == null) {
            return false;
        }
        return productoRepository.findById(productoId)
                .map(p -> p.getLocalComida() != null && isOwnerOrNull(p.getLocalComida(), userId))
                .orElse(false);
    }

    private boolean isOwnerOrNull(LocalComida local, UUID userId) {
        return local.getDuenoId() == null || userId.equals(local.getDuenoId());
    }
}
