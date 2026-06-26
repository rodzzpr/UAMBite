package com.example.uambite.repository;

import com.example.uambite.model.Entrega;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface EntregaRepository extends JpaRepository<Entrega, UUID> {
}