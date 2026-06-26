package com.example.uambite.controller;

import com.example.uambite.dto.request.IngredienteExtraRequest;
import com.example.uambite.dto.response.IngredienteExtraResponse;
import com.example.uambite.service.IngredienteExtraService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/ingredienteextra")
public class IngredienteExtraController {

    private final IngredienteExtraService service;

    public IngredienteExtraController(IngredienteExtraService service) {
        this.service = service;
    }

    @GetMapping("/all")
    public List<IngredienteExtraResponse> getAll() {
        return service.getAll();
    }

    @PostMapping("/save")
    public IngredienteExtraResponse save(@RequestBody IngredienteExtraRequest request) {
        return service.save(request);
    }
}