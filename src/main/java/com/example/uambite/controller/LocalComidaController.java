package com.example.uambite.controller;

import com.example.uambite.dto.request.EncargadoCreateRequest;
import com.example.uambite.dto.request.LocalComidaConEncargadoRequest;
import com.example.uambite.dto.request.LocalComidaRequest;
import com.example.uambite.dto.response.LocalComidaConEncargadoResponse;
import com.example.uambite.dto.response.LocalComidaResponse;
import com.example.uambite.service.LocalComidaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/localcomida")
@RequiredArgsConstructor
@Tag(name = "Locales de Comida", description = "Gestión de locales de comida")
public class LocalComidaController {

    private final LocalComidaService service;

    @GetMapping("/all")
    @Operation(summary = "Listar todos los locales de comida")
    public ResponseEntity<List<LocalComidaResponse>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener un local por ID")
    public ResponseEntity<LocalComidaResponse> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PostMapping("/save")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Crear un nuevo local de comida junto con su encargado (atómico)")
    public ResponseEntity<LocalComidaConEncargadoResponse> save(
            @Valid @RequestBody LocalComidaConEncargadoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.crearLocalConEncargado(request));
    }

    @PutMapping("/update/{id}")
    @PreAuthorize("hasRole('ADMIN') or @ownershipService.canEditLocal(#id, principal.id)")
    @Operation(summary = "Actualizar un local de comida (solo el nombre/ubicación/horario)")
    public ResponseEntity<LocalComidaResponse> update(@PathVariable UUID id,
                                                      @Valid @RequestBody LocalComidaRequest request) {
        return ResponseEntity.ok(service.update(id, request));
    }

    @PutMapping("/{id}/asignar-encargado")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Asignar o reasignar el encargado de un local. Crea el usuario si no existe.")
    public ResponseEntity<LocalComidaConEncargadoResponse> asignarEncargado(
            @PathVariable UUID id, @Valid @RequestBody EncargadoCreateRequest request) {
        return ResponseEntity.ok(service.asignarEncargado(id, request));
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Eliminar un local de comida")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
