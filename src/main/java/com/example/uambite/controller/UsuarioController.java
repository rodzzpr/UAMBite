package com.example.uambite.controller;

import com.example.uambite.dto.request.UsuarioRequest;
import com.example.uambite.dto.response.UsuarioResponse;
import com.example.uambite.service.UsuarioService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/usuario")
public class UsuarioController {

    private final UsuarioService service;

    public UsuarioController(UsuarioService service) {
        this.service = service;
    }

    @GetMapping("/all")
    public List<UsuarioResponse> getAll() {
        return service.getAll();
    }

    @PostMapping("/save")
    public UsuarioResponse save(@RequestBody UsuarioRequest request) {
        return service.save(request);
    }

}