package com.example.uambite.controller;

import com.example.uambite.model.Producto;
import com.example.uambite.service.ProductoService;
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
    public List<Producto> getAll() {
        return service.getAll();
    }

    @PostMapping("/save")
    public Producto save(@RequestBody Producto producto) {
        return service.save(producto);
    }
}