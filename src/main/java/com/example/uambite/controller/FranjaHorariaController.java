package com.example.uambite.controller;

import com.example.uambite.dto.request.FranjaHorariaRequest;
import com.example.uambite.dto.response.FranjaHorariaResponse;
import com.example.uambite.service.FranjaHorariaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/franja")
@RequiredArgsConstructor
@Tag(name = "Franjas Horarias", description = "Gestión de franjas horarias con control de capacidad")
public class FranjaHorariaController {

    private final FranjaHorariaService service;

    @GetMapping("/all")
    @Operation(summary = "Listar todas las franjas horarias")
    public ResponseEntity<List<FranjaHorariaResponse>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/disponibles")
    @Operation(summary = "Listar franjas horarias con capacidad disponible")
    public ResponseEntity<List<FranjaHorariaResponse>> getDisponibles() {
        return ResponseEntity.ok(service.getDisponibles());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener una franja por ID")
    public ResponseEntity<FranjaHorariaResponse> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PostMapping("/save")
    @Operation(summary = "Crear una nueva franja horaria")
    public ResponseEntity<FranjaHorariaResponse> save(@Valid @RequestBody FranjaHorariaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.save(request));
    }

    @PutMapping("/update/{id}")
    @Operation(summary = "Actualizar una franja horaria")
    public ResponseEntity<FranjaHorariaResponse> update(@PathVariable UUID id,
                                                        @Valid @RequestBody FranjaHorariaRequest request) {
        return ResponseEntity.ok(service.update(id, request));
    }

    @DeleteMapping("/delete/{id}")
    @Operation(summary = "Eliminar una franja horaria")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
