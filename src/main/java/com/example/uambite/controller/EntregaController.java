package com.example.uambite.controller;

import com.example.uambite.dto.request.EntregaRequest;
import com.example.uambite.dto.response.EntregaResponse;
import com.example.uambite.model.Rol;
import com.example.uambite.model.Usuario;
import com.example.uambite.service.EntregaService;
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
@RequestMapping("/entrega")
@RequiredArgsConstructor
@Tag(name = "Entregas", description = "Gestión de entregas de pedidos")
public class EntregaController {

    private final EntregaService service;

    @GetMapping("/all")
    @PreAuthorize("hasAnyRole('ADMIN', 'LOCAL')")
    @Operation(summary = "Listar entregas. ADMIN ve todas; LOCAL ve las de pedidos de sus locales.")
    public ResponseEntity<List<EntregaResponse>> getAll() {
        Usuario principal = usuarioAutenticado();
        if (principal.getRol() == Rol.LOCAL) {
            return ResponseEntity.ok(service.getAllForLocalOwner(principal.getId()));
        }
        return ResponseEntity.ok(service.getAll());
    }

    @PostMapping("/save")
    @PreAuthorize("hasRole('ADMIN') or @ownershipService.canManagePedido(#request.pedidoId, principal.id)")
    @Operation(summary = "Registrar la entrega de un pedido pagado. ADMIN o encargado del local del pedido.")
    public ResponseEntity<EntregaResponse> save(@Valid @RequestBody EntregaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.save(request));
    }

    @PutMapping("/finalizar/{id}")
    @PreAuthorize("hasRole('ADMIN') or @ownershipService.canManageEntrega(#id, principal.id)")
    @Operation(summary = "Finalizar una entrega (EN_CAMINO → ENTREGADO). ADMIN o encargado del local del pedido.")
    public ResponseEntity<EntregaResponse> finalizarEntrega(@PathVariable UUID id) {
        return ResponseEntity.ok(service.finalizarEntrega(id));
    }

    private Usuario usuarioAutenticado() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return (Usuario) auth.getPrincipal();
    }
}


