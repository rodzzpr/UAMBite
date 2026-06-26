package com.example.uambite.service;

import com.example.uambite.dto.request.RegisterRequest;
import com.example.uambite.dto.request.UsuarioRequest;
import com.example.uambite.dto.response.UsuarioResponse;
import com.example.uambite.exceptions.ConflictException;
import com.example.uambite.exceptions.ResourceNotFoundException;
import com.example.uambite.model.Usuario;
import com.example.uambite.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository repository;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public List<UsuarioResponse> getAll() {
        return repository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public Optional<Usuario> findByCarnet(String carnet) {
        return repository.findByCarnet(carnet);
    }

    @Transactional
    public UsuarioResponse register(String carnet, String nombre, String apellido,
                                    String password, String rol) {
        if (repository.findByCarnet(carnet).isPresent()) {
            throw new ConflictException("Ya existe un usuario registrado con ese carnet.");
        }
        Usuario usuario = Usuario.builder()
                .carnet(carnet)
                .nombre(nombre)
                .apellido(apellido)
                .password(passwordEncoder.encode(password))
                .rol(rol)
                .build();
        return toResponse(repository.save(usuario));
    }

    @Transactional
    public UsuarioResponse save(UsuarioRequest request) {
        return register(request.getCarnet(), request.getNombre(), request.getApellido(),
                request.getPassword(), "CLIENTE");
    }

    @Transactional(readOnly = true)
    public UsuarioResponse getById(UUID id) {
        return toResponse(repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado.")));
    }

    @Transactional
    public UsuarioResponse update(UUID id, UsuarioRequest request) {
        Usuario usuario = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado."));
        usuario.setNombre(request.getNombre());
        usuario.setApellido(request.getApellido());
        usuario.setCorreo(request.getCorreo());
        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            usuario.setPassword(passwordEncoder.encode(request.getPassword()));
        }
        return toResponse(repository.save(usuario));
    }

    private UsuarioResponse toResponse(Usuario usuario) {
        return UsuarioResponse.builder()
                .id(usuario.getId())
                .carnet(usuario.getCarnet())
                .nombre(usuario.getNombre())
                .apellido(usuario.getApellido())
                .correo(usuario.getCorreo())
                .rol(usuario.getRol())
                .build();
    }
}
