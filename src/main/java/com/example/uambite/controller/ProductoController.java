package com.example.uambite.controller;

import com.example.uambite.dto.request.ProductoRequest;
import com.example.uambite.dto.response.ProductoResponse;
import com.example.uambite.service.ProductoService;
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
@RequestMapping("/producto")
@RequiredArgsConstructor
@Tag(name = "Productos", description = "Gestión de productos")
public class ProductoController {

    private final ProductoService service;

    @GetMapping("/all")
    @Operation(summary = "Listar todos los productos")
    public ResponseEntity<List<ProductoResponse>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener un producto por ID")
    public ResponseEntity<ProductoResponse> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PostMapping("/save")
    @PreAuthorize("hasRole('ADMIN') or @ownershipService.canEditLocal(#request.localComidaId, principal.id)")
    @Operation(summary = "Crear un nuevo producto. ADMIN o encargado del local destino.")
    public ResponseEntity<ProductoResponse> save(@Valid @RequestBody ProductoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.save(request));
    }

    @PutMapping("/update/{id}")
    @PreAuthorize("hasRole('ADMIN') or @ownershipService.canEditProducto(#id, principal.id)")
    @Operation(summary = "Actualizar un producto. ADMIN o encargado del local del producto.")
    public ResponseEntity<ProductoResponse> update(@PathVariable UUID id,
                                                  @Valid @RequestBody ProductoRequest request) {
        return ResponseEntity.ok(service.update(id, request));
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasRole('ADMIN') or @ownershipService.canEditProducto(#id, principal.id)")
    @Operation(summary = "Eliminar un producto. ADMIN o encargado del local del producto.")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}

