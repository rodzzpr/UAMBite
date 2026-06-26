package com.example.uambite.controller;

import com.example.uambite.dto.request.FranjaHorariaRequest;
import com.example.uambite.dto.response.FranjaHorariaResponse;
import com.example.uambite.service.FranjaHorariaService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/franja")
public class FranjaHorariaController {

    private final FranjaHorariaService service;

    public FranjaHorariaController(FranjaHorariaService service) {
        this.service = service;
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/disponibles")
    public List<FranjaHorariaResponse> getDisponibles() {
        return service.getDisponibles();
    }

    @PostMapping("/save")
    public ResponseEntity<?> save(@Valid @RequestBody FranjaHorariaRequest request) {
        return ResponseEntity.ok(service.save(request));
    }
}
