package com.example.uambite.repository;

import com.example.uambite.model.Pago;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PagoRepository extends JpaRepository<Pago, UUID> {
}