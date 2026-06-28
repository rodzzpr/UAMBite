package com.example.uambite.repository;

import com.example.uambite.model.FranjaHoraria;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface FranjaHorariaRepository extends JpaRepository<FranjaHoraria, UUID> {

    List<FranjaHoraria> findByDisponibleTrue();

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT f FROM FranjaHoraria f WHERE f.id = :id")
    Optional<FranjaHoraria> findByIdWithLock(@Param("id") UUID id);

}
