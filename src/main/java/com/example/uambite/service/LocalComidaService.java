package com.example.uambite.service;

import com.example.uambite.dto.request.EncargadoCreateRequest;
import com.example.uambite.dto.request.LocalComidaConEncargadoRequest;
import com.example.uambite.dto.request.LocalComidaRequest;
import com.example.uambite.dto.response.LocalComidaConEncargadoResponse;
import com.example.uambite.dto.response.LocalComidaResponse;
import com.example.uambite.dto.response.UsuarioResponse;
import com.example.uambite.exceptions.BusinessException;
import com.example.uambite.exceptions.ConflictException;
import com.example.uambite.exceptions.ResourceNotFoundException;
import com.example.uambite.model.LocalComida;
import com.example.uambite.model.Rol;
import com.example.uambite.model.Usuario;
import com.example.uambite.repository.LocalComidaRepository;
import com.example.uambite.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LocalComidaService {

    private final LocalComidaRepository repository;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public List<LocalComidaResponse> getAll() {
        return repository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public LocalComidaResponse getById(UUID id) {
        return toResponse(findOrThrow(id));
    }

    @Transactional
    public LocalComidaConEncargadoResponse crearLocalConEncargado(LocalComidaConEncargadoRequest request) {
        if (usuarioRepository.findByCarnet(request.getEncargado().getCarnet()).isPresent()) {
            throw new ConflictException(
                    "Ya existe un usuario con el carnet " + request.getEncargado().getCarnet()
                            + ". Use /asignar-encargado si desea reasignar el local a un usuario existente.");
        }
        Usuario encargado = Usuario.builder()
                .carnet(request.getEncargado().getCarnet())
                .nombre(request.getEncargado().getNombre())
                .apellido(request.getEncargado().getApellido())
                .correo(request.getEncargado().getCorreo())
                .password(passwordEncoder.encode(request.getEncargado().getPassword()))
                .rol(Rol.LOCAL)
                .build();
        encargado = usuarioRepository.save(encargado);

        LocalComida local = LocalComida.builder()
                .nombre(request.getNombre())
                .ubicacion(request.getUbicacion())
                .horario(request.getHorario())
                .duenoId(encargado.getId())
                .build();
        local = repository.save(local);

        return LocalComidaConEncargadoResponse.builder()
                .local(toResponse(local))
                .encargado(toEncargadoResponse(encargado))
                .build();
    }

    @Transactional
    public LocalComidaResponse update(UUID id, LocalComidaRequest request) {
        LocalComida local = findOrThrow(id);
        local.setNombre(request.getNombre());
        local.setUbicacion(request.getUbicacion());
        local.setHorario(request.getHorario());
        return toResponse(repository.save(local));
    }

    @Transactional
    public LocalComidaConEncargadoResponse asignarEncargado(UUID localId, EncargadoCreateRequest request) {
        LocalComida local = findOrThrow(localId);
        Usuario encargado = usuarioRepository.findByCarnet(request.getCarnet())
                .orElseGet(() -> usuarioRepository.save(Usuario.builder()
                        .carnet(request.getCarnet())
                        .nombre(request.getNombre())
                        .apellido(request.getApellido())
                        .correo(request.getCorreo())
                        .password(passwordEncoder.encode(request.getPassword()))
                        .rol(Rol.LOCAL)
                        .build()));
        if (encargado.getRol() != Rol.LOCAL) {
            throw new BusinessException(
                    "El usuario " + encargado.getCarnet() + " no tiene rol LOCAL; no puede ser asignado como encargado.",
                    HttpStatus.CONFLICT, "ROLE_MISMATCH");
        }
        local.setDuenoId(encargado.getId());
        LocalComida saved = repository.save(local);
        return LocalComidaConEncargadoResponse.builder()
                .local(toResponse(saved))
                .encargado(toEncargadoResponse(encargado))
                .build();
    }

    @Transactional
    public void delete(UUID id) {
        LocalComida local = findOrThrow(id);
        repository.delete(local);
    }

    private LocalComida findOrThrow(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Local de comida no encontrado."));
    }

    private LocalComidaResponse toResponse(LocalComida local) {
        return LocalComidaResponse.builder()
                .id(local.getId())
                .nombre(local.getNombre())
                .ubicacion(local.getUbicacion())
                .horario(local.getHorario())
                .duenoId(local.getDuenoId())
                .build();
    }

    private UsuarioResponse toEncargadoResponse(Usuario u) {
        return UsuarioResponse.builder()
                .id(u.getId())
                .carnet(u.getCarnet())
                .nombre(u.getNombre())
                .apellido(u.getApellido())
                .correo(u.getCorreo())
                .rol(u.getRol())
                .build();
    }
}
