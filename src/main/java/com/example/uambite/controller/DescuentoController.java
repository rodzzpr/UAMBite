package com.example.uambite.controller;

import com.example.uambite.dto.request.DescuentoRequest;
import com.example.uambite.dto.response.DescuentoResponse;
import com.example.uambite.service.DescuentoService;
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
@RequestMapping("/descuento")
@RequiredArgsConstructor
@Tag(name = "Descuentos", description = "Cupones de descuento")
public class DescuentoController {

    private final DescuentoService service;

    @GetMapping("/all")
    @Operation(summary = "Listar todos los descuentos")
    public ResponseEntity<Page<DescuentoResponse>> getAll(Pageable pageable) {
        return ResponseEntity.ok(service.getAll(pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener un descuento por ID")
    public ResponseEntity<DescuentoResponse> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PostMapping("/save")
    @PreAuthorize("hasRole('ADMIN') or (#request.localComidaId != null and @ownershipService.canEditLocal(#request.localComidaId, principal.id))")
    @Operation(summary = "Crear un nuevo descuento. ADMIN o encargado del local asociado.")
    public ResponseEntity<DescuentoResponse> save(@Valid @RequestBody DescuentoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.save(request));
    }

    @PutMapping("/update/{id}")
    @PreAuthorize("hasRole('ADMIN') or @ownershipService.canEditDescuento(#id, principal.id)")
    @Operation(summary = "Actualizar un descuento. ADMIN o encargado del local del descuento.")
    public ResponseEntity<DescuentoResponse> update(@PathVariable UUID id,
                                                    @Valid @RequestBody DescuentoRequest request) {
        return ResponseEntity.ok(service.update(id, request));
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasRole('ADMIN') or @ownershipService.canEditDescuento(#id, principal.id)")
    @Operation(summary = "Eliminar un descuento. ADMIN o encargado del local del descuento.")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}

