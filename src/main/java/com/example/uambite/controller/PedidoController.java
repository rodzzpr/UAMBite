package com.example.uambite.controller;

import com.example.uambite.dto.request.PedidoRequest;
import com.example.uambite.dto.response.PedidoResponse;
import com.example.uambite.service.PedidoService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/pedido")
public class PedidoController {

    private final PedidoService service;

    public PedidoController(PedidoService service) {
        this.service = service;
    }

    @GetMapping("/all")
    public List<PedidoResponse> getAll() {
        return service.getAll();
    }

    @PostMapping("/save")
    public PedidoResponse save(@RequestBody PedidoRequest request) {
        return service.save(request);
    }
}