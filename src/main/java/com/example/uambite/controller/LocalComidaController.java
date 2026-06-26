package com.example.uambite.controller;

import com.example.uambite.dto.request.LocalComidaRequest;
import com.example.uambite.dto.response.LocalComidaResponse;
import com.example.uambite.service.LocalComidaService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/localcomida")
public class LocalComidaController {

    private final LocalComidaService service;

    public LocalComidaController(LocalComidaService service) {
        this.service = service;
    }

    @GetMapping("/all")
    public List<LocalComidaResponse> getAll() {
        return service.getAll();
    }

    @PostMapping("/save")
    public LocalComidaResponse save(@RequestBody LocalComidaRequest request) {
        return service.save(request);
    }
}