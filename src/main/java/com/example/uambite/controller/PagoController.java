package com.example.uambite.controller;

import com.example.uambite.dto.request.PagoRequest;
import com.example.uambite.dto.response.PagoResponse;
import com.example.uambite.service.PagoService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/pago")
public class PagoController {

    private final PagoService service;

    public PagoController(PagoService service) {
        this.service = service;
    }

    @GetMapping("/all")
    public List<PagoResponse> getAll() {
        return service.getAll();
    }

    @PostMapping("/save")
    public PagoResponse save(@RequestBody PagoRequest request) {
        return service.save(request);
    }
}