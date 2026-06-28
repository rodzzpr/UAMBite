package com.example.uambite.controller;

import com.example.uambite.dto.request.CambioEstadoPedidoRequest;
import com.example.uambite.dto.request.PedidoRequest;
import com.example.uambite.dto.request.PrioridadRequest;
import com.example.uambite.dto.response.PedidoResponse;
import com.example.uambite.model.Rol;
import com.example.uambite.model.Usuario;
import com.example.uambite.service.PedidoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
    @PreAuthorize("hasAnyRole('ADMIN', 'LOCAL')")
    @Operation(summary = "Listar pedidos. ADMIN ve todos; LOCAL ve los de sus locales (ordenados por prioridad).")
    public ResponseEntity<List<PedidoResponse>> getAll() {
        Usuario principal = usuarioAutenticado();
        if (principal.getRol() == Rol.LOCAL) {
            return ResponseEntity.ok(service.getAllForLocalOwner(principal.getId()));
        }
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @ownershipService.canViewPedido(#id, principal.id)")
    @Operation(summary = "Obtener un pedido por ID. ADMIN, dueño del pedido o encargado del local del pedido.")
    public ResponseEntity<PedidoResponse> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PostMapping("/save")
    @PreAuthorize("hasRole('ADMIN') or #request.usuarioId == null or #request.usuarioId == principal.id")
    @Operation(summary = "Crear un nuevo pedido. CLIENTE/ESTUDIANTE/PROFESOR solo para sí mismo; ADMIN puede crear para otro.")
    public ResponseEntity<PedidoResponse> save(@Valid @RequestBody PedidoRequest request) {
        Usuario principal = usuarioAutenticado();
        if (principal.getRol() != Rol.ADMIN) {
            request.setUsuarioId(principal.getId());
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(service.save(request));
    }

    @PutMapping("/update/{id}")
    @PreAuthorize("hasRole('ADMIN') or @ownershipService.canViewPedido(#id, principal.id)")
    @Operation(summary = "Actualizar tipo de entrega de un pedido PENDIENTE. ADMIN, dueño del pedido o encargado del local.")
    public ResponseEntity<PedidoResponse> update(@PathVariable UUID id,
                                                @Valid @RequestBody PedidoRequest request) {
        return ResponseEntity.ok(service.update(id, request));
    }

    @PutMapping("/{id}/estado")
    @PreAuthorize("hasRole('ADMIN') or "
            + "(#request.estado.name() == 'CANCELADO' || #request.estado.name() == 'ENTREGADO'"
            + " ? @ownershipService.canViewPedido(#id, principal.id)"
            + " : @ownershipService.canManagePedido(#id, principal.id))")
    @Operation(summary = "Cambiar el estado de un pedido. CANCELADO: ADMIN, dueño o encargado. Otros: ADMIN o encargado del local.")
    public ResponseEntity<PedidoResponse> cambiarEstado(@PathVariable UUID id,
                                                        @Valid @RequestBody CambioEstadoPedidoRequest request) {
        return ResponseEntity.ok(service.cambiarEstado(id, request));
    }

    @PutMapping("/{id}/prioridad")
    @PreAuthorize("hasRole('ADMIN') or @ownershipService.canManagePedido(#id, principal.id)")
    @Operation(summary = "Establecer la prioridad del pedido (mayor número = más prioritario). Solo ADMIN o encargado del local.")
    public ResponseEntity<PedidoResponse> setPrioridad(@PathVariable UUID id,
                                                       @Valid @RequestBody PrioridadRequest body) {
        return ResponseEntity.ok(service.setPrioridad(id, body.getPrioridad()));
    }

    @Deprecated
    @PutMapping("/confirmar/{id}")
    @PreAuthorize("hasRole('ADMIN') or @ownershipService.canManagePedido(#id, principal.id)")
    @Operation(summary = "DEPRECADO. Use PUT /pedido/{id}/estado con {\"estado\": \"CONFIRMADO\"}.")
    public ResponseEntity<PedidoResponse> confirmar(@PathVariable UUID id) {
        return ResponseEntity.ok(service.confirmarPedido(id));
    }

    @Deprecated
    @PutMapping("/preparar/{id}")
    @PreAuthorize("hasRole('ADMIN') or @ownershipService.canManagePedido(#id, principal.id)")
    @Operation(summary = "DEPRECADO. Use PUT /pedido/{id}/estado con {\"estado\": \"EN_PREPARACION\"}.")
    public ResponseEntity<PedidoResponse> preparar(@PathVariable UUID id) {
        return ResponseEntity.ok(service.marcarEnPreparacion(id));
    }

    @Deprecated
    @PutMapping("/listo/{id}")
    @PreAuthorize("hasRole('ADMIN') or @ownershipService.canManagePedido(#id, principal.id)")
    @Operation(summary = "DEPRECADO. Use PUT /pedido/{id}/estado con {\"estado\": \"LISTO\"}.")
    public ResponseEntity<PedidoResponse> listo(@PathVariable UUID id) {
        return ResponseEntity.ok(service.marcarListo(id));
    }

    @Deprecated
    @PutMapping("/cancelar/{id}")
    @PreAuthorize("hasRole('ADMIN') or @ownershipService.canViewPedido(#id, principal.id)")
    @Operation(summary = "DEPRECADO. Use PUT /pedido/{id}/estado con {\"estado\": \"CANCELADO\"}.")
    public ResponseEntity<PedidoResponse> cancelar(@PathVariable UUID id) {
        return ResponseEntity.ok(service.cancelarPedido(id));
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasRole('ADMIN') or @ownershipService.canViewPedido(#id, principal.id)")
    @Operation(summary = "Eliminar pedido PENDIENTE o CANCELADO. ADMIN o dueño del pedido.")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/mios")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Listar los pedidos del usuario autenticado")
    public ResponseEntity<List<PedidoResponse>> getMios() {
        return ResponseEntity.ok(service.getMios(usuarioAutenticado().getId()));
    }

    private Usuario usuarioAutenticado() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return (Usuario) auth.getPrincipal();
    }
}
