package com.example.uambite.repository;

import com.example.uambite.model.DetallePedido;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface DetallePedidoRepository extends JpaRepository<DetallePedido, UUID> {

    List<DetallePedido> findByPedidoId(UUID pedidoId);

}