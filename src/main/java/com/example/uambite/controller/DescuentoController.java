package com.example.uambite.controller;

import com.example.uambite.dto.request.DescuentoRequest;
import com.example.uambite.dto.response.DescuentoResponse;
import com.example.uambite.service.DescuentoService;
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
@RequestMapping("/descuento")
@RequiredArgsConstructor
@Tag(name = "Descuentos", description = "Cupones de descuento")
public class DescuentoController {

    private final DescuentoService service;

    @GetMapping("/all")
    @Operation(summary = "Listar todos los descuentos")
    public ResponseEntity<List<DescuentoResponse>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener un descuento por ID")
    public ResponseEntity<DescuentoResponse> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PostMapping("/save")
    @Operation(summary = "Crear un nuevo descuento")
    public ResponseEntity<DescuentoResponse> save(@Valid @RequestBody DescuentoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.save(request));
    }

    @PutMapping("/update/{id}")
    @Operation(summary = "Actualizar un descuento")
    public ResponseEntity<DescuentoResponse> update(@PathVariable UUID id,
                                                    @Valid @RequestBody DescuentoRequest request) {
        return ResponseEntity.ok(service.update(id, request));
    }

    @DeleteMapping("/delete/{id}")
    @Operation(summary = "Eliminar un descuento")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
