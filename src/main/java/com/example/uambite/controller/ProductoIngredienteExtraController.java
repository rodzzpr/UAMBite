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
    @Operation(summary = "Asociar un ingrediente extra a un producto")
    public ResponseEntity<ProductoIngredienteExtraResponse> save(
            @Valid @RequestBody ProductoIngredienteExtraRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.save(request));
    }

    @DeleteMapping("/delete/{id}")
    @Operation(summary = "Eliminar una asociación")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
