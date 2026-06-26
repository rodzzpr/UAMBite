package com.example.uambite.repository;

import com.example.uambite.model.EstadoPedido;
import com.example.uambite.model.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface PedidoRepository extends JpaRepository<Pedido, UUID> {

    List<Pedido> findByUsuarioIdAndEstadoIn(UUID usuarioId, List<EstadoPedido> estados);

    List<Pedido> findByUsuarioId(UUID usuarioId);

}
