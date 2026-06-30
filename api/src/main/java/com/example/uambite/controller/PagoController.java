package com.example.uambite.controller;

import com.example.uambite.dto.request.PagoRequest;
import com.example.uambite.dto.response.PagoResponse;
import com.example.uambite.service.PagoService;
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
@RequestMapping("/pago")
@RequiredArgsConstructor
@Tag(name = "Pagos", description = "Gestión de pagos de pedidos")
public class PagoController {

    private final PagoService service;

    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Listar todos los pagos. Solo ADMIN.")
    public ResponseEntity<Page<PagoResponse>> getAll(Pageable pageable) {
        return ResponseEntity.ok(service.getAll(pageable));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @ownershipService.canViewPago(#id, principal.id)")
    @Operation(summary = "Obtener un pago por ID. ADMIN o dueño del pedido asociado.")
    public ResponseEntity<PagoResponse> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PostMapping("/save")
    @PreAuthorize("hasRole('ADMIN') or @ownershipService.canViewPedido(#request.pedidoId, principal.id)")
    @Operation(summary = "Registrar el pago de un pedido PENDIENTE. ADMIN o dueño del pedido.")
    public ResponseEntity<PagoResponse> save(@Valid @RequestBody PagoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.save(request));
    }
}
