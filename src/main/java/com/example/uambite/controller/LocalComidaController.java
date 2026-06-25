package com.example.uambite.controller;

import com.example.uambite.model.LocalComida;
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
    public List<LocalComida> getAll() {
        return service.getAll();
    }

    @PostMapping("/save")
    public LocalComida save(@RequestBody LocalComida localComida) {
        return service.save(localComida);
    }

}