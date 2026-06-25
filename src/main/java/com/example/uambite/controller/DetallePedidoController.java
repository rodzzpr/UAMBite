package com.example.uambite.controller;

import com.example.uambite.model.DetallePedido;
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
    public List<DetallePedido> getAll() {
        return service.getAll();
    }

    @PostMapping("/save")
    public DetallePedido save(@RequestBody DetallePedido detallePedido) {
        return service.save(detallePedido);
    }

}