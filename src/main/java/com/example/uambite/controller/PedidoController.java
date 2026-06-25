package com.example.uambite.controller;

import com.example.uambite.model.Pedido;
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
    public List<Pedido> getAll() {
        return service.getAll();
    }

    @PostMapping("/save")
    public Pedido save(@RequestBody Pedido pedido) {
        return service.save(pedido);
    }
}