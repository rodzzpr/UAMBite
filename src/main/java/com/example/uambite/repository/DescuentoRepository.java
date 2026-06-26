package com.example.uambite.repository;

import com.example.uambite.model.Descuento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface DescuentoRepository extends JpaRepository<Descuento, UUID> {

    Optional<Descuento> findByCodigo(String codigo);

}