package com.example.uambite.repository;

import com.example.uambite.model.ProductoIngredienteExtra;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ProductoIngredienteExtraRepository
        extends JpaRepository<ProductoIngredienteExtra, UUID> {

    Optional<ProductoIngredienteExtra> findByProductoIdAndIngredienteExtraId(
            UUID productoId,
            UUID ingredienteExtraId
    );
}