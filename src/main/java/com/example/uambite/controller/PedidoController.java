package com.example.uambite.controller;

import com.example.uambite.dto.request.PedidoRequest;
import com.example.uambite.dto.response.PedidoResponse;
import com.example.uambite.service.PedidoService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/pedido")
public class PedidoController {

    private final PedidoService service;

    public PedidoController(PedidoService service) {
        this.service = service;
    }

    @GetMapping("/all")
    public List<PedidoResponse> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public PedidoResponse getById(@PathVariable UUID id) {
        return service.getById(id);
    }

    @PostMapping("/save")
    public PedidoResponse save(@Valid @RequestBody PedidoRequest request) {
        return service.save(request);
    }

    @PutMapping("/update/{id}")
    public PedidoResponse update(@PathVariable UUID id,
                                  @Valid @RequestBody PedidoRequest request) {
        return service.update(id, request);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.ok("Pedido eliminado correctamente.");
    }

    @PutMapping("/cancelar/{id}")
    public PedidoResponse cancelarPedido(@PathVariable UUID id) {
        return service.cancelarPedido(id);
    }
}
