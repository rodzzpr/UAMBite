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

        // ── setup: crear ADMIN real en BD (fase 2 requiere ADMIN para crear locales) ─
        Usuario admin = new Usuario();
        admin.setCarnet("A000000");
        admin.setNombre("Admin");
        admin.setApellido("E2E");
        admin.setRol(Rol.ADMIN);
        admin.setPassword("$2a$10$dummy");
        admin = usuarioRepository.save(admin);
        RequestPostProcessor authAdmin = authAs(admin.getId(), Rol.ADMIN);
        System.out.println("✅ [setup] ADMIN persistido:   " + admin.getId());

        // ── setup: crear ESTUDIANTE real en BD (cliente que pide) ─
        Usuario u = new Usuario();
        u.setCarnet("U999999");
        u.setNombre("Test");
        u.setApellido("E2E");
        u.setRol(Rol.ESTUDIANTE);
        u.setPassword("$2a$10$dummy");
        u = usuarioRepository.save(u);
        UUID usuarioId = u.getId();
        RequestPostProcessor auth = authAs(usuarioId, Rol.ESTUDIANTE);
        System.out.println("✅ [setup] ESTUDIANTE persistido: " + usuarioId);

        // ── 1. Crear local + encargado (flujo unificado fase 2) ───
        MvcResult r1 = mockMvc.perform(post("/localcomida/save")
                        .with(authAdmin).contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                LocalComidaConEncargadoRequest.builder()
                                        .nombre("Local E2E")
                                        .ubicacion("Av. Universidad 123")
                                        .horario("L-V 8:00-18:00")
                                        .encargado(EncargadoCreateRequest.builder()
                                                .carnet("encargado01")
                                                .nombre("Encargado")
                                                .apellido("Local E2E")
                                                .correo("encargado@local.local")
                                                .password("secret123")
                                                .build())
                                        .build())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.local.nombre").value("Local E2E"))
                .andExpect(jsonPath("$.encargado.rol").value("LOCAL"))
                .andExpect(jsonPath("$.encargado.carnet").value("encargado01"))
                .andExpect(jsonPath("$.local.duenoId").exists())
                .andReturn();
        LocalComidaConEncargadoResponse r1Body = parse(r1, LocalComidaConEncargadoResponse.class);
        UUID localId = r1Body.getLocal().getId();
        UUID duenoId = r1Body.getEncargado().getId();
        RequestPostProcessor authEncargado = authAs(duenoId, Rol.LOCAL);
        System.out.println("✅ [1/9] Local+encargado:    local=" + localId + " dueno=" + duenoId);

        // ── 2. Crear producto (ENCARGADO) ────────────────────────
        MvcResult r2 = mockMvc.perform(post("/producto/save")
                        .with(authEncargado).contentType(MediaType.APPLICATION_JSON)
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

        // ── 3. Crear franja horaria (ENCARGADO) ──────────────────
        MvcResult r3 = mockMvc.perform(post("/franja/save")
                        .with(authEncargado).contentType(MediaType.APPLICATION_JSON)
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

        // ── 4. Crear pedido (ESTUDIANTE) ─────────────────────────
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

        // ── 5. Agregar detalle (ESTUDIANTE) ──────────────────────
        mockMvc.perform(post("/detallepedido/save")
                        .with(auth).contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                DetallePedidoRequest.builder()
                                        .cantidad(2)
                                        .pedidoId(pedidoId)
                                        .productoId(productoId).build())))
                .andExpect(status().isCreated());
        System.out.println("✅ [5/9] Detalle agregado:    2 hamburguesas");

        // ── 6. Verificar total (ESTUDIANTE) y confirmar (ENCARGADO) → decrementa stock ─
        mockMvc.perform(get("/pedido/{id}", pedidoId).with(auth))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(51.00));

        mockMvc.perform(put("/pedido/confirmar/{id}", pedidoId).with(authEncargado))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("CONFIRMADO"));

        mockMvc.perform(get("/producto/{id}", productoId).with(auth))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stock").value(8));
        System.out.println("✅ [6/9] CONFIRMADO + stock:  10 → 8");

        // ── 7. Pagar (ESTUDIANTE) ────────────────────────────────
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

        // ── 8. Transiciones de estado (ENCARGADO) ────────────────
        mockMvc.perform(put("/pedido/preparar/{id}", pedidoId).with(authEncargado))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("EN_PREPARACION"));
        mockMvc.perform(put("/pedido/listo/{id}", pedidoId).with(authEncargado))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("LISTO"));
        System.out.println("✅ [8/9] Estados:            CONFIRMADO → EN_PREPARACION → LISTO");

        // ── 9. Crear entrega y finalizar (ENCARGADO) ─────────────
        MvcResult r9a = mockMvc.perform(post("/entrega/save")
                        .with(authEncargado).contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                EntregaRequest.builder()
                                        .ubicacion("Mostrador principal")
                                        .pedidoId(pedidoId).build())))
                .andExpect(status().isCreated()).andReturn();
        UUID entregaId = parse(r9a, EntregaResponse.class).getId();

        mockMvc.perform(put("/entrega/finalizar/{id}", entregaId).with(authEncargado))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("ENTREGADA"));

        mockMvc.perform(get("/pedido/{id}", pedidoId).with(auth))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("ENTREGADO"));
        System.out.println("✅ [9/9] Entrega ENTREGADA:   pedido → ENTREGADO");

        // ── 10. Verificaciones de seguridad (fase 3) ───────────
        // a) Un ESTUDIANTE no puede crear un pedido para otro user.
        PedidoRequest ajeno = PedidoRequest.builder()
                .tipoEntrega(TipoEntrega.RETIRO_LOCAL)
                .usuarioId(UUID.randomUUID())
                .build();
        mockMvc.perform(post("/pedido/save")
                        .with(auth).contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ajeno)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value("ACCESS_DENIED"));
        System.out.println("✅ [10a] Seguridad: ESTUDIANTE no puede crear pedido para otro user");

        // b) Un ESTUDIANTE no puede crear productos.
        mockMvc.perform(post("/producto/save")
                        .with(auth).contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                ProductoRequest.builder()
                                        .nombre("X").precio(new BigDecimal("1.00")).stock(1)
                                        .permitePersonalizacion(false).localComidaId(localId)
                                        .build())))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value("ACCESS_DENIED"));
        System.out.println("✅ [10b] Seguridad: ESTUDIANTE no puede crear productos");

        // c) Un ESTUDIANTE no puede confirmar pedidos.
        mockMvc.perform(put("/pedido/confirmar/{id}", pedidoId).with(auth))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value("ACCESS_DENIED"));
        System.out.println("✅ [10c] Seguridad: ESTUDIANTE no puede confirmar pedidos");

        // d) Un ENCARGADO de otro local (otro LOCAL) no puede confirmar.
        Usuario otroLocal = new Usuario();
        otroLocal.setCarnet("otroLocal01");
        otroLocal.setNombre("Otro");
        otroLocal.setApellido("Local");
        otroLocal.setRol(Rol.LOCAL);
        otroLocal.setPassword("$2a$10$dummy");
        otroLocal = usuarioRepository.save(otroLocal);
        mockMvc.perform(put("/pedido/confirmar/{id}", pedidoId).with(authAs(otroLocal.getId(), Rol.LOCAL)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value("ACCESS_DENIED"));
        System.out.println("✅ [10d] Seguridad: ENCARGADO de otro local no puede confirmar");

        System.out.println("\n🎉 FLUJO E2E COMPLETO EXITOSO\n" + "=".repeat(50));
        System.out.println("Estados transitados:");
        System.out.println("  PENDIENTE → CONFIRMADO → EN_PREPARACION → LISTO → ENTREGADO");
        System.out.println("Validaciones verificadas:");
        System.out.println("  ✓ Stock decrementado al confirmar (10 → 8)");
        System.out.println("  ✓ Total = subtotal sin descuento ($51.00)");
        System.out.println("  ✓ Pago registrado en estado PAGADO");
        System.out.println("  ✓ Entrega ENTREGADA → pedido ENTREGADO");
        System.out.println("  ✓ RBAC: ESTUDIANTE/LOCAL no acceden a recursos de otros");
    }

    private <T> T parse(MvcResult result, Class<T> clazz) throws Exception {
        return objectMapper.readValue(result.getResponse().getContentAsString(), clazz);
    }

    private RequestPostProcessor authAs(UUID userId, Rol rol) {
        return request -> {
            Usuario u = new Usuario();
            u.setId(userId);
            u.setCarnet("U999999");
            u.setRol(rol);
            SecurityContext ctx = SecurityContextHolder.createEmptyContext();
            ctx.setAuthentication(new UsernamePasswordAuthenticationToken(
                    u, null,
                    Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + rol.name()))));
            SecurityContextHolder.setContext(ctx);
            return request;
        };
    }
}
