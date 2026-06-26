package com.example.uambite.controller;

import com.example.uambite.dto.request.EntregaRequest;
import com.example.uambite.dto.response.EntregaResponse;
import com.example.uambite.service.EntregaService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/entrega")
public class EntregaController {

    private final EntregaService service;

    public EntregaController(EntregaService service) {
        this.service = service;
    }

    @GetMapping("/all")
    public List<EntregaResponse> getAll() {
        return service.getAll();
    }

    @PostMapping("/save")
    public EntregaResponse save(@Valid @RequestBody EntregaRequest request) {
        return service.save(request);
    }

    @PutMapping("/finalizar/{id}")
    public EntregaResponse finalizarEntrega(@PathVariable UUID id) {
        return service.finalizarEntrega(id);
    }
}
