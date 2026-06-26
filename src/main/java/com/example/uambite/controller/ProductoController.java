package com.example.uambite.controller;

import com.example.uambite.dto.request.ProductoRequest;
import com.example.uambite.dto.response.ProductoResponse;
import com.example.uambite.service.ProductoService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/producto")
public class ProductoController {

    private final ProductoService service;

    public ProductoController(ProductoService service) {
        this.service = service;
    }

    @GetMapping("/all")
    public List<ProductoResponse> getAll() {
        return service.getAll();
    }

    @PostMapping("/save")
    public ProductoResponse save(@Valid @RequestBody ProductoRequest request) {
        return service.save(request);
    }
}
