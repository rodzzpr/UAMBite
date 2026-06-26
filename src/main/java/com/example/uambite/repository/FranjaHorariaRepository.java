package com.example.uambite.repository;

import com.example.uambite.model.FranjaHoraria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface FranjaHorariaRepository extends JpaRepository<FranjaHoraria, UUID> {

    List<FranjaHoraria> findByDisponibleTrue();

}
