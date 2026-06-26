package com.example.uambite.controller;

import com.example.uambite.dto.request.PedidoRequest;
import com.example.uambite.dto.response.PedidoResponse;
import com.example.uambite.model.Usuario;
import com.example.uambite.service.PedidoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/pedido")
@RequiredArgsConstructor
@Tag(name = "Pedidos", description = "Gestión y flujo de estados de pedidos")
public class PedidoController {

    private final PedidoService service;

    @GetMapping("/all")
    @Operation(summary = "Listar todos los pedidos")
    public ResponseEntity<List<PedidoResponse>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener un pedido por ID")
    public ResponseEntity<PedidoResponse> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PostMapping("/save")
    @Operation(summary = "Crear un nuevo pedido (estado PENDIENTE)")
    public ResponseEntity<PedidoResponse> save(@Valid @RequestBody PedidoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.save(request));
    }

    @PutMapping("/update/{id}")
    @Operation(summary = "Actualizar tipo de entrega de un pedido PENDIENTE")
    public ResponseEntity<PedidoResponse> update(@PathVariable UUID id,
                                                @Valid @RequestBody PedidoRequest request) {
        return ResponseEntity.ok(service.update(id, request));
    }

    @PutMapping("/confirmar/{id}")
    @Operation(summary = "Confirmar un pedido (PENDIENTE → CONFIRMADO, descuenta stock)")
    public ResponseEntity<PedidoResponse> confirmar(@PathVariable UUID id) {
        return ResponseEntity.ok(service.confirmarPedido(id));
    }

    @PutMapping("/preparar/{id}")
    @Operation(summary = "Marcar pedido en preparación (CONFIRMADO → EN_PREPARACION)")
    public ResponseEntity<PedidoResponse> preparar(@PathVariable UUID id) {
        return ResponseEntity.ok(service.marcarEnPreparacion(id));
    }

    @PutMapping("/listo/{id}")
    @Operation(summary = "Marcar pedido como listo (EN_PREPARACION → LISTO)")
    public ResponseEntity<PedidoResponse> listo(@PathVariable UUID id) {
        return ResponseEntity.ok(service.marcarListo(id));
    }

    @PutMapping("/cancelar/{id}")
    @Operation(summary = "Cancelar un pedido (solo si no está en preparación, listo o entregado)")
    public ResponseEntity<PedidoResponse> cancelar(@PathVariable UUID id) {
        return ResponseEntity.ok(service.cancelarPedido(id));
    }

    @DeleteMapping("/delete/{id}")
    @Operation(summary = "Eliminar un pedido PENDIENTE o CANCELADO")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/mios")
    @Operation(summary = "Listar los pedidos del usuario autenticado")
    public ResponseEntity<List<PedidoResponse>> getMios() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Usuario usuario = (Usuario) auth.getPrincipal();
        return ResponseEntity.ok(service.getMios(usuario.getId()));
    }
}
