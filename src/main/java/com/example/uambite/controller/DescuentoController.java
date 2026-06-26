package com.example.uambite.controller;

import com.example.uambite.dto.request.DescuentoRequest;
import com.example.uambite.dto.response.DescuentoResponse;
import com.example.uambite.service.DescuentoService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/descuento")
public class DescuentoController {

    private final DescuentoService service;

    public DescuentoController(DescuentoService service) {
        this.service = service;
    }

    @GetMapping("/all")
    public List<DescuentoResponse> getAll() {
        return service.getAll();
    }

    @PostMapping("/save")
    public DescuentoResponse save(@RequestBody DescuentoRequest request) {
        return service.save(request);
    }
}
