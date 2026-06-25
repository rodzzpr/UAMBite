package com.example.uambite.service;

import com.example.uambite.model.Usuario;
import com.example.uambite.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UsuarioService {

    private final UsuarioRepository repository;

    public UsuarioService(UsuarioRepository repository) {
        this.repository = repository;
    }

    public List<Usuario> getAll() {
        return repository.findAll();
    }

    public Usuario save(Usuario usuario) {
        return repository.save(usuario);
    }
}