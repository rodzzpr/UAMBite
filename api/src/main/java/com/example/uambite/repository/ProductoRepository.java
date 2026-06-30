package com.example.uambite.repository;

import com.example.uambite.model.Producto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.UUID;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, UUID> {

    Page<Producto> findByLocalComidaId(UUID localComidaId, Pageable pageable);

    Page<Producto> findByNombreContainingIgnoreCase(String nombre, Pageable pageable);

    Page<Producto> findByLocalComidaIdAndNombreContainingIgnoreCase(UUID localComidaId, String nombre, Pageable pageable);

    Page<Producto> findByPrecioLessThanEqual(BigDecimal maxPrecio, Pageable pageable);

    Page<Producto> findByLocalComidaIdAndPrecioLessThanEqual(UUID localComidaId, BigDecimal maxPrecio, Pageable pageable);

    Page<Producto> findByNombreContainingIgnoreCaseAndPrecioLessThanEqual(String nombre, BigDecimal maxPrecio, Pageable pageable);

    Page<Producto> findByLocalComidaIdAndNombreContainingIgnoreCaseAndPrecioLessThanEqual(
            UUID localComidaId, String nombre, BigDecimal maxPrecio, Pageable pageable);

}
