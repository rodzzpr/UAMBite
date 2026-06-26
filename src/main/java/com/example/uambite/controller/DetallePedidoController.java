package com.example.uambite.controller;

import com.example.uambite.dto.request.DetallePedidoRequest;
import com.example.uambite.dto.response.DetallePedidoResponse;
import com.example.uambite.service.DetallePedidoService;
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
@RequestMapping("/detallepedido")
@RequiredArgsConstructor
@Tag(name = "Detalles de Pedido", description = "Líneas de pedido (productos + extras)")
public class DetallePedidoController {

    private final DetallePedidoService service;

    @GetMapping("/all")
    @Operation(summary = "Listar todos los detalles de pedido")
    public ResponseEntity<List<DetallePedidoResponse>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @PostMapping("/save")
    @Operation(summary = "Agregar un detalle (producto + ingredientes extra) a un pedido PENDIENTE")
    public ResponseEntity<DetallePedidoResponse> save(@Valid @RequestBody DetallePedidoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.save(request));
    }

    @DeleteMapping("/delete/{id}")
    @Operation(summary = "Eliminar un detalle de un pedido PENDIENTE")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
