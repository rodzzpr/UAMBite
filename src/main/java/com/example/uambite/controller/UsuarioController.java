package com.example.uambite.controller;

import com.example.uambite.model.Usuario;
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
    public List<Usuario> getAll() {
        return service.getAll();
    }

    @PostMapping("/save")
    public Usuario save(@RequestBody Usuario usuario) {
        return service.save(usuario);
    }
}