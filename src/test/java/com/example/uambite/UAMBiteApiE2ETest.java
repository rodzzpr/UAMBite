package com.example.uambite;

import com.example.uambite.dto.request.*;
import com.example.uambite.dto.response.*;
import com.example.uambite.model.*;
import com.example.uambite.repository.UsuarioRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.Collections;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test E2E del flujo completo de la API en un solo método.
 * H2 en memoria + usuario persistido en BD (sin mocks).
 *
 * Ejecuta: mvn test -Dtest=UAMBiteApiE2ETest
 */
@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class UAMBiteApiE2ETest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private UsuarioRepository usuarioRepository;

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @Test
    @DisplayName("Flujo completo: local → producto → pedido → pago → entrega")
    void flujoCompleto() throws Exception {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();

        System.out.println("\n🚀 INICIANDO FLUJO E2E DE UAMBITE\n" + "=".repeat(50));

        // ── setup: crear usuario real en BD ─────────────────────
        Usuario u = new Usuario();
        u.setCarnet("U999999");
        u.setNombre("Test");
        u.setApellido("E2E");
        u.setRol("CLIENTE");
        u.setPassword("$2a$10$dummy");
        u = usuarioRepository.save(u);
        UUID usuarioId = u.getId();
        RequestPostProcessor auth = authAs(usuarioId, "CLIENTE");
        System.out.println("✅ [setup] Usuario persistido: " + usuarioId);

        // ── 1. Crear local ────────────────────────────────────────
        MvcResult r1 = mockMvc.perform(post("/localcomida/save")
                        .with(auth).contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                LocalComidaRequest.builder()
                                        .nombre("Local E2E")
                                        .ubicacion("Av. Universidad 123")
                                        .horario("L-V 8:00-18:00").build())))
                .andExpect(status().isCreated()).andReturn();
        UUID localId = parse(r1, LocalComidaResponse.class).getId();
        System.out.println("✅ [1/9] Local creado:        " + localId);

        // ── 2. Crear producto ────────────────────────────────────
        MvcResult r2 = mockMvc.perform(post("/producto/save")
                        .with(auth).contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                ProductoRequest.builder()
                                        .nombre("Hamburguesa")
                                        .descripcion("Doble carne")
                                        .precio(new BigDecimal("25.50"))
                                        .stock(10)
                                        .permitePersonalizacion(true)
                                        .localComidaId(localId).build())))
                .andExpect(status().isCreated()).andReturn();
        UUID productoId = parse(r2, ProductoResponse.class).getId();
        System.out.println("✅ [2/9] Producto creado:     " + productoId + " (precio=$25.50, stock=10)");

        // ── 3. Crear franja horaria ─────────────────────────────
        MvcResult r3 = mockMvc.perform(post("/franja/save")
                        .with(auth).contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                FranjaHorariaRequest.builder()
                                        .horaInicio(LocalTime.of(12, 0))
                                        .horaFin(LocalTime.of(14, 0))
                                        .capacidadMaxima(50)
                                        .disponible(true)
                                        .localComidaId(localId).build())))
                .andExpect(status().isCreated()).andReturn();
        UUID franjaId = parse(r3, FranjaHorariaResponse.class).getId();
        System.out.println("✅ [3/9] Franja creada:       " + franjaId + " (12:00-14:00, cap=50)");

        // ── 4. Crear pedido ──────────────────────────────────────
        MvcResult r4 = mockMvc.perform(post("/pedido/save")
                        .with(auth).contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                PedidoRequest.builder()
                                        .tipoEntrega(TipoEntrega.RETIRO_LOCAL)
                                        .usuarioId(usuarioId)
                                        .franjaHorariaId(franjaId).build())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.estado").value("PENDIENTE"))
                .andReturn();
        UUID pedidoId = parse(r4, PedidoResponse.class).getId();
        System.out.println("✅ [4/9] Pedido creado:       " + pedidoId + " (estado=PENDIENTE)");

        // ── 5. Agregar detalle (2 hamburguesas) ─────────────────
        mockMvc.perform(post("/detallepedido/save")
                        .with(auth).contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                DetallePedidoRequest.builder()
                                        .cantidad(2)
                                        .pedidoId(pedidoId)
                                        .productoId(productoId).build())))
                .andExpect(status().isCreated());
        System.out.println("✅ [5/9] Detalle agregado:    2 hamburguesas");

        // ── 6. Verificar total y confirmar → decrementa stock ───
        mockMvc.perform(get("/pedido/{id}", pedidoId).with(auth))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(51.00));

        mockMvc.perform(put("/pedido/confirmar/{id}", pedidoId).with(auth))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("CONFIRMADO"));

        mockMvc.perform(get("/producto/{id}", productoId).with(auth))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stock").value(8));
        System.out.println("✅ [6/9] CONFIRMADO + stock:  10 → 8");

        // ── 7. Pagar ────────────────────────────────────────────
        MvcResult r7 = mockMvc.perform(post("/pago/save")
                        .with(auth).contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                PagoRequest.builder()
                                        .metodoPago(MetodoPago.EFECTIVO)
                                        .pedidoId(pedidoId).build())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.estado").value("PAGADO"))
                .andReturn();
        PagoResponse pago = parse(r7, PagoResponse.class);
        System.out.println("✅ [7/9] Pago registrado:    monto=$" + pago.getMonto() + " (PAGADO)");

        // ── 8. Transiciones de estado ───────────────────────────
        mockMvc.perform(put("/pedido/preparar/{id}", pedidoId).with(auth))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("EN_PREPARACION"));
        mockMvc.perform(put("/pedido/listo/{id}", pedidoId).with(auth))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("LISTO"));
        System.out.println("✅ [8/9] Estados:            CONFIRMADO → EN_PREPARACION → LISTO");

        // ── 9. Crear entrega y finalizar ───────────────────────
        MvcResult r9a = mockMvc.perform(post("/entrega/save")
                        .with(auth).contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                EntregaRequest.builder()
                                        .ubicacion("Mostrador principal")
                                        .pedidoId(pedidoId).build())))
                .andExpect(status().isCreated()).andReturn();
        UUID entregaId = parse(r9a, EntregaResponse.class).getId();

        mockMvc.perform(put("/entrega/finalizar/{id}", entregaId).with(auth))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("ENTREGADA"));

        mockMvc.perform(get("/pedido/{id}", pedidoId).with(auth))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("ENTREGADO"));
        System.out.println("✅ [9/9] Entrega ENTREGADA:   pedido → ENTREGADO");
        System.out.println("\n🎉 FLUJO E2E COMPLETO EXITOSO\n" + "=".repeat(50));
        System.out.println("Estados transitados:");
        System.out.println("  PENDIENTE → CONFIRMADO → EN_PREPARACION → LISTO → ENTREGADO");
        System.out.println("Validaciones verificadas:");
        System.out.println("  ✓ Stock decrementado al confirmar (10 → 8)");
        System.out.println("  ✓ Total = subtotal sin descuento ($51.00)");
        System.out.println("  ✓ Pago registrado en estado PAGADO");
        System.out.println("  ✓ Entrega ENTREGADA → pedido ENTREGADO");
    }

    private <T> T parse(MvcResult result, Class<T> clazz) throws Exception {
        return objectMapper.readValue(result.getResponse().getContentAsString(), clazz);
    }

    private RequestPostProcessor authAs(UUID userId, String rol) {
        return request -> {
            Usuario u = new Usuario();
            u.setId(userId);
            u.setCarnet("U999999");
            u.setRol(rol);
            SecurityContext ctx = SecurityContextHolder.createEmptyContext();
            ctx.setAuthentication(new UsernamePasswordAuthenticationToken(
                    u, null,
                    Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + rol))));
            SecurityContextHolder.setContext(ctx);
            return request;
        };
    }
}
