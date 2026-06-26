package com.example.uambite.controller;

import com.example.uambite.dto.request.ProductoIngredienteExtraRequest;
import com.example.uambite.dto.response.ProductoIngredienteExtraResponse;
import com.example.uambite.service.ProductoIngredienteExtraService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/productoingredienteextra")
public class ProductoIngredienteExtraController {

    private final ProductoIngredienteExtraService service;

    public ProductoIngredienteExtraController(
            ProductoIngredienteExtraService service) {
        this.service = service;
    }

    @GetMapping("/all")
    public List<ProductoIngredienteExtraResponse> getAll() {
        return service.getAll();
    }

    @PostMapping("/save")
    public ProductoIngredienteExtraResponse save(
            @RequestBody ProductoIngredienteExtraRequest request) {

        return service.save(request);
    }
}