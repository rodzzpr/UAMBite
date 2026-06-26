package com.example.uambite.repository;

import com.example.uambite.model.DetallePedidoIngredienteExtra;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface DetallePedidoIngredienteExtraRepository
        extends JpaRepository<DetallePedidoIngredienteExtra, UUID> {

    List<DetallePedidoIngredienteExtra> findByDetallePedidoId(UUID detallePedidoId);
}
