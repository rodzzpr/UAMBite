package com.example.uambite.repository;

import com.example.uambite.model.EstadoPedido;
import com.example.uambite.model.Pedido;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface PedidoRepository extends JpaRepository<Pedido, UUID> {

    List<Pedido> findByUsuarioIdAndEstadoIn(UUID usuarioId, List<EstadoPedido> estados);

    Page<Pedido> findByUsuarioIdAndEstadoIn(UUID usuarioId, List<EstadoPedido> estados, Pageable pageable);

    List<Pedido> findByUsuarioId(UUID usuarioId);

    Page<Pedido> findByUsuarioId(UUID usuarioId, Pageable pageable);

    List<Pedido> findByLocalComidaIdInOrderByPrioridadDescCreatedAtAsc(Collection<UUID> localComidaIds);

    Page<Pedido> findByLocalComidaIdIn(Collection<UUID> localComidaIds, Pageable pageable);

}
