package com.example.uambite;

import com.example.uambite.controller.*;
import com.example.uambite.dto.request.*;
import com.example.uambite.dto.response.*;
import com.example.uambite.exceptions.BusinessException;
import com.example.uambite.exceptions.ConflictException;
import com.example.uambite.model.*;
import com.example.uambite.security.JwtUtil;
import com.example.uambite.service.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {
        DetallePedidoController.class,
        PedidoController.class,
        EntregaController.class,
        FranjaHorariaController.class
})
@AutoConfigureMockMvc(addFilters = false)
class UAMBiteApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DetallePedidoService detallePedidoService;

    @MockitoBean
    private PedidoService pedidoService;

    @MockitoBean
    private EntregaService entregaService;

    @MockitoBean
    private FranjaHorariaService franjaHorariaService;

    @MockitoBean
    private UsuarioService usuarioService;

    @MockitoBean
    private JwtUtil jwtUtil;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    void testStockInsuficiente() throws Exception {
        DetallePedidoRequest request = new DetallePedidoRequest();
        request.setCantidad(100);
        request.setPedidoId(UUID.randomUUID());
        request.setProductoId(UUID.randomUUID());

        when(detallePedidoService.save(any()))
                .thenThrow(new ConflictException("Stock insuficiente para el producto seleccionado."));

        mockMvc.perform(post("/detallepedido/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Stock insuficiente para el producto seleccionado."))
                .andExpect(jsonPath("$.code").value("CONFLICT"));
    }

    @Test
    void testPedidoActivo() throws Exception {
        PedidoRequest request = new PedidoRequest();
        request.setTipoEntrega(TipoEntrega.RETIRO_LOCAL);
        request.setUsuarioId(UUID.randomUUID());

        when(pedidoService.save(any()))
                .thenThrow(new ConflictException("El usuario ya tiene un pedido activo."));

        mockMvc.perform(post("/pedido/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("El usuario ya tiene un pedido activo."))
                .andExpect(jsonPath("$.code").value("CONFLICT"));
    }

    @Test
    void testFranjaLlena() throws Exception {
        PedidoRequest request = new PedidoRequest();
        request.setTipoEntrega(TipoEntrega.RETIRO_LOCAL);
        request.setUsuarioId(UUID.randomUUID());
        request.setFranjaHorariaId(UUID.randomUUID());

        when(pedidoService.save(any()))
                .thenThrow(new ConflictException("La franja horaria está llena."));

        mockMvc.perform(post("/pedido/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("La franja horaria está llena."))
                .andExpect(jsonPath("$.code").value("CONFLICT"));
    }

    @Test
    void testFinalizarEntrega() throws Exception {
        UUID entregaId = UUID.randomUUID();

        EntregaResponse response = new EntregaResponse();
        response.setId(entregaId);
        response.setUbicacion("Av. Principal 123");
        response.setEstado(EstadoEntrega.ENTREGADA);

        when(entregaService.finalizarEntrega(entregaId)).thenReturn(response);

        mockMvc.perform(put("/entrega/finalizar/{id}", entregaId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("ENTREGADA"))
                .andExpect(jsonPath("$.ubicacion").value("Av. Principal 123"));
    }

    @Test
    void testFranjaHorariaDisponibles() throws Exception {
        mockMvc.perform(get("/franja/disponibles"))
                .andExpect(status().isOk());
    }

    @Test
    void testCancelarPedido() throws Exception {
        UUID pedidoId = UUID.randomUUID();

        PedidoResponse response = new PedidoResponse();
        response.setId(pedidoId);
        response.setEstado(EstadoPedido.CANCELADO);

        when(pedidoService.cancelarPedido(pedidoId)).thenReturn(response);

        mockMvc.perform(put("/pedido/cancelar/{id}", pedidoId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("CANCELADO"));
    }
}
