package com.example.uambite.controller;

import com.example.uambite.dto.request.DetallePedidoRequest;
import com.example.uambite.dto.response.DetallePedidoResponse;
import com.example.uambite.service.DetallePedidoService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/detallepedido")
public class DetallePedidoController {

    private final DetallePedidoService service;

    public DetallePedidoController(DetallePedidoService service) {
        this.service = service;
    }

    @GetMapping("/all")
    public List<DetallePedidoResponse> getAll() {
        return service.getAll();
    }

    @PostMapping("/save")
    public DetallePedidoResponse save(@RequestBody DetallePedidoRequest request) {
        return service.save(request);
    }
}