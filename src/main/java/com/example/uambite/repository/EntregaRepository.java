package com.example.uambite.repository;

import com.example.uambite.model.Entrega;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface EntregaRepository extends JpaRepository<Entrega, UUID> {

    List<Entrega> findByPedido_LocalComidaIdIn(Collection<UUID> localComidaIds);

}