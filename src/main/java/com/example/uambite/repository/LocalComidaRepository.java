package com.example.uambite.repository;

import com.example.uambite.model.LocalComida;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface LocalComidaRepository extends JpaRepository<LocalComida, UUID> {

}