package com.example.uambite.controller;

import com.example.uambite.dto.request.ProductoIngredienteExtraRequest;
import com.example.uambite.dto.response.ProductoIngredienteExtraResponse;
import com.example.uambite.service.ProductoIngredienteExtraService;
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
@RequestMapping("/productoingredienteextra")
@RequiredArgsConstructor
@Tag(name = "Producto-Ingrediente", description = "Asociación producto ↔ ingrediente extra")
public class ProductoIngredienteExtraController {

    private final ProductoIngredienteExtraService service;

    @GetMapping("/all")
    @Operation(summary = "Listar todas las asociaciones")
    public ResponseEntity<List<ProductoIngredienteExtraResponse>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @PostMapping("/save")
    @PreAuthorize("hasRole('ADMIN') or @ownershipService.canEditLocalByProducto(#request.productoId, principal.id)")
    @Operation(summary = "Asociar un ingrediente extra a un producto. ADMIN o encargado del local del producto.")
    public ResponseEntity<ProductoIngredienteExtraResponse> save(
            @Valid @RequestBody ProductoIngredienteExtraRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.save(request));
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasRole('ADMIN') or @ownershipService.canEditLocalByProducto(#id, principal.id)")
    @Operation(summary = "Eliminar una asociación. ADMIN o encargado del local del producto asociado.")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}

