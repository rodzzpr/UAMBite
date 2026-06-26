package com.example.uambite.controller;

import com.example.uambite.dto.request.UsuarioRequest;
import com.example.uambite.dto.response.UsuarioResponse;
import com.example.uambite.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/usuario")
@RequiredArgsConstructor
@Tag(name = "Usuarios", description = "Gestión de usuarios")
public class UsuarioController {

    private final UsuarioService service;

    @GetMapping("/all")
    @Operation(summary = "Listar todos los usuarios")
    public ResponseEntity<List<UsuarioResponse>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener un usuario por ID")
    public ResponseEntity<UsuarioResponse> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PostMapping("/save")
    @Operation(summary = "Crear un nuevo usuario (rol CLIENTE por defecto)")
    public ResponseEntity<UsuarioResponse> save(@Valid @RequestBody UsuarioRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.save(request));
    }

    @PutMapping("/update/{id}")
    @Operation(summary = "Actualizar un usuario existente")
    public ResponseEntity<UsuarioResponse> update(@PathVariable UUID id,
                                                  @Valid @RequestBody UsuarioRequest request) {
        return ResponseEntity.ok(service.update(id, request));
    }
}
