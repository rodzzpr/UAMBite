package com.example.uambite.controller;

import com.example.uambite.dto.request.IngredienteExtraRequest;
import com.example.uambite.dto.response.IngredienteExtraResponse;
import com.example.uambite.service.IngredienteExtraService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.http.HttpStatus;

import org.springframework.http.ResponseEntity;

import org.springframework.security.access.prepost.PreAuthorize;

import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/ingredienteextra")
@RequiredArgsConstructor
@Tag(name = "Ingredientes Extra", description = "Catálogo de ingredientes extra")
public class IngredienteExtraController {

    private final IngredienteExtraService service;

    @GetMapping("/all")
    @Operation(summary = "Listar todos los ingredientes extra")
    public ResponseEntity<Page<IngredienteExtraResponse>> getAll(Pageable pageable) {
        return ResponseEntity.ok(service.getAll(pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener un ingrediente extra por ID")
    public ResponseEntity<IngredienteExtraResponse> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PostMapping("/save")
    @PreAuthorize("hasAnyRole('ADMIN', 'LOCAL')")
    @Operation(summary = "Crear un nuevo ingrediente extra. ADMIN o LOCAL.")
    public ResponseEntity<IngredienteExtraResponse> save(@Valid @RequestBody IngredienteExtraRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.save(request));
    }

    @PutMapping("/update/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'LOCAL')")
    @Operation(summary = "Actualizar un ingrediente extra. ADMIN o LOCAL.")
    public ResponseEntity<IngredienteExtraResponse> update(@PathVariable UUID id,
                                                          @Valid @RequestBody IngredienteExtraRequest request) {
        return ResponseEntity.ok(service.update(id, request));
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'LOCAL')")
    @Operation(summary = "Eliminar un ingrediente extra. ADMIN o LOCAL.")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}

