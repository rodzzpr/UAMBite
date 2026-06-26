package com.example.uambite.repository;

import com.example.uambite.model.IngredienteExtra;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface IngredienteExtraRepository extends JpaRepository<IngredienteExtra, UUID> {

}