package com.example.uambite.service;

import com.example.uambite.dto.request.UsuarioRequest;
import com.example.uambite.dto.response.UsuarioResponse;
import com.example.uambite.model.Usuario;
import com.example.uambite.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UsuarioService {

    private final UsuarioRepository repository;

    public UsuarioService(UsuarioRepository repository) {
        this.repository = repository;
    }

    // Obtener todos los usuarios
    public List<UsuarioResponse> getAll() {
        return repository.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // Guardar usuario
    public UsuarioResponse save(UsuarioRequest request) {

        Usuario usuario = new Usuario();

        usuario.setNombre(request.getNombre());
        usuario.setCorreo(request.getCorreo());
        usuario.setRol(request.getRol());

        Usuario saved = repository.save(usuario);

        return toResponse(saved);
    }

    // Conversión Entity -> DTO
    private UsuarioResponse toResponse(Usuario usuario) {

        UsuarioResponse response = new UsuarioResponse();

        response.setId(usuario.getId());
        response.setNombre(usuario.getNombre());
        response.setCorreo(usuario.getCorreo());
        response.setRol(usuario.getRol());

        return response;
    }

}