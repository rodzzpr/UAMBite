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
import org.springframework.security.access.prepost.PreAuthorize;
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
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Listar todos los detalles de pedido. Solo ADMIN.")
    public ResponseEntity<List<DetallePedidoResponse>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @PostMapping("/save")
    @PreAuthorize("hasRole('ADMIN') or @ownershipService.canViewPedido(#request.pedidoId, principal.id)")
    @Operation(summary = "Agregar un detalle a un pedido. ADMIN, dueño del pedido o encargado del local.")
    public ResponseEntity<DetallePedidoResponse> save(@Valid @RequestBody DetallePedidoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.save(request));
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasRole('ADMIN') or @ownershipService.canManagePedido(#id, principal.id) or @ownershipService.canViewPedido(#id, principal.id)")
    @Operation(summary = "Eliminar un detalle de un pedido PENDIENTE. ADMIN, dueño del pedido o encargado del local.")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}

