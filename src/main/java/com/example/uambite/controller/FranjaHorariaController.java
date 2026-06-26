package com.example.uambite.controller;

import com.example.uambite.dto.request.FranjaHorariaRequest;
import com.example.uambite.service.FranjaHorariaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping("/save")
    public ResponseEntity<?> save(@RequestBody FranjaHorariaRequest request) {
        return ResponseEntity.ok(service.save(request));
    }
}
