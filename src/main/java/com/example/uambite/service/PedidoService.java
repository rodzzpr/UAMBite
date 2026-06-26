package com.example.uambite.service;

import com.example.uambite.dto.request.PedidoRequest;
import com.example.uambite.dto.response.PedidoResponse;
import com.example.uambite.model.Pedido;
import com.example.uambite.model.Usuario;
import com.example.uambite.repository.PedidoRepository;
import com.example.uambite.repository.UsuarioRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PedidoService {

    private final PedidoRepository repository;
    private final UsuarioRepository usuarioRepository;

    public PedidoService(PedidoRepository repository,
                         UsuarioRepository usuarioRepository) {
        this.repository = repository;
        this.usuarioRepository = usuarioRepository;
    }

    // Obtener todos los pedidos
    public List<PedidoResponse> getAll() {
        return repository.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // Crear pedido
    public PedidoResponse save(PedidoRequest request) {

        Usuario usuario = usuarioRepository.findById(request.getUsuarioId())
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado."));

        Pedido pedido = new Pedido();

        // Datos enviados por el cliente
        pedido.setTipoEntrega(request.getTipoEntrega());
        pedido.setUsuario(usuario);

        // Datos generados por el sistema
        pedido.setEstado("PENDIENTE");
        pedido.setTotal(0.0);

        Pedido saved = repository.save(pedido);

        return toResponse(saved);
    }

    // Entity -> DTO
    private PedidoResponse toResponse(Pedido pedido) {

        PedidoResponse response = new PedidoResponse();

        response.setId(pedido.getId());
        response.setEstado(pedido.getEstado());
        response.setTotal(pedido.getTotal());
        response.setTipoEntrega(pedido.getTipoEntrega());

        if (pedido.getUsuario() != null) {
            response.setUsuario(pedido.getUsuario().getNombre());
        }

        return response;
    }
}