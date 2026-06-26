package com.example.uambite.controller;

import com.example.uambite.dto.request.EntregaRequest;
import com.example.uambite.dto.response.EntregaResponse;
import com.example.uambite.service.EntregaService;
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
@RequestMapping("/entrega")
@RequiredArgsConstructor
@Tag(name = "Entregas", description = "Gestión de entregas de pedidos")
public class EntregaController {

    private final EntregaService service;

    @GetMapping("/all")
    @Operation(summary = "Listar todas las entregas")
    public ResponseEntity<List<EntregaResponse>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @PostMapping("/save")
    @Operation(summary = "Registrar la entrega de un pedido pagado")
    public ResponseEntity<EntregaResponse> save(@Valid @RequestBody EntregaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.save(request));
    }

    @PutMapping("/finalizar/{id}")
    @Operation(summary = "Finalizar una entrega (EN_CAMINO → ENTREGADO)")
    public ResponseEntity<EntregaResponse> finalizarEntrega(@PathVariable UUID id) {
        return ResponseEntity.ok(service.finalizarEntrega(id));
    }
}
