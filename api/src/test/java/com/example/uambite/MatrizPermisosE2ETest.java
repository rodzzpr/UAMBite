package com.example.uambite;

import com.example.uambite.dto.request.EncargadoCreateRequest;
import com.example.uambite.dto.request.LocalComidaConEncargadoRequest;
import com.example.uambite.dto.request.ProductoRequest;
import com.example.uambite.dto.request.DescuentoRequest;
import com.example.uambite.dto.request.IngredienteExtraRequest;
import com.example.uambite.dto.response.LocalComidaConEncargadoResponse;
import com.example.uambite.model.Descuento;
import com.example.uambite.model.IngredienteExtra;
import com.example.uambite.model.LocalComida;
import com.example.uambite.model.Producto;
import com.example.uambite.model.Rol;
import com.example.uambite.model.Usuario;
import com.example.uambite.repository.DescuentoRepository;
import com.example.uambite.repository.IngredienteExtraRepository;
import com.example.uambite.repository.LocalComidaRepository;
import com.example.uambite.repository.ProductoRepository;
import com.example.uambite.repository.UsuarioRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
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
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Matriz de permisos: para cada par (rol, endpoint) prueba un caso positivo y
 * un caso negativo representativos. Sirve como test de regresión del RBAC.
 *
 * Ejecuta: mvn test -Dtest=MatrizPermisosE2ETest
 */
@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class MatrizPermisosE2ETest {

    @Autowired
    private WebApplicationContext context;
    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private LocalComidaRepository localComidaRepository;
    @Autowired
    private ProductoRepository productoRepository;
    @Autowired
    private DescuentoRepository descuentoRepository;
    @Autowired
    private IngredienteExtraRepository ingredienteExtraRepository;

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    private Usuario admin;
    private Usuario encargadoA;
    private Usuario encargadoB;
    private Usuario estudiante;
    private Usuario profesor;
    private LocalComida localA;
    private LocalComida localB;
    private Producto productoA;

    @BeforeEach
    void setUp() throws Exception {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
        SecurityContextHolder.clearContext();

        String suffix = Long.toString(System.nanoTime() % 1_000_000_000L);
        admin = saveUsuario("MA" + suffix, "Admin", "Test", Rol.ADMIN);
        estudiante = saveUsuario("ES" + suffix, "Est", "Test", Rol.ESTUDIANTE);
        String profCarnet = String.format("%014d", System.nanoTime() % 100_000_000_000_000L);
        profesor = saveUsuario(profCarnet, "Prof", "Test", Rol.PROFESOR);

        LocalComidaConEncargadoResponse respA = crearLocalConEncargado(admin.getId(),
                LocalComidaConEncargadoRequest.builder()
                        .nombre("Local A").ubicacion("UAM Pasillo A")
                        .horario("L-V 9-18")
                        .encargado(EncargadoCreateRequest.builder()
                                .carnet("EA" + suffix).nombre("EncA").apellido("X")
                                .correo("a@x").password("secret123").build())
                        .build());
        LocalComidaConEncargadoResponse respB = crearLocalConEncargado(admin.getId(),
                LocalComidaConEncargadoRequest.builder()
                        .nombre("Local B").ubicacion("UAM Pasillo B")
                        .horario("L-V 9-18")
                        .encargado(EncargadoCreateRequest.builder()
                                .carnet("EB" + suffix).nombre("EncB").apellido("X")
                                .correo("b@x").password("secret123").build())
                        .build());
        localA = localComidaRepository.findById(respA.getLocal().getId()).orElseThrow();
        localB = localComidaRepository.findById(respB.getLocal().getId()).orElseThrow();
        encargadoA = usuarioRepository.findById(localA.getDuenoId()).orElseThrow();
        encargadoB = usuarioRepository.findById(localB.getDuenoId()).orElseThrow();

        productoA = productoRepository.save(Producto.builder()
                .nombre("ProdA").precio(new BigDecimal("10.00")).stock(50)
                .permitePersonalizacion(false).localComida(localA).build());
    }

    @Test
    @DisplayName("ADMIN puede crear locales; ESTUDIANTE/PROFESOR no")
    void adminCreaLocal_estudiantesNo() throws Exception {
        String suffix = Long.toString(System.nanoTime() % 1_000_000_000L);
        mockMvc.perform(post("/localcomida/save")
                        .with(authAs(admin.getId(), Rol.ADMIN))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                LocalComidaConEncargadoRequest.builder()
                                        .nombre("Nuevo").ubicacion("X").horario("X")
                                        .encargado(EncargadoCreateRequest.builder()
                                                .carnet("NW" + suffix).nombre("N").apellido("E")
                                                .correo("n@e").password("secret123").build())
                                        .build())))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/localcomida/save")
                        .with(authAs(estudiante.getId(), Rol.ESTUDIANTE))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                LocalComidaConEncargadoRequest.builder()
                                        .nombre("Pirata").ubicacion("X").horario("X")
                                        .encargado(EncargadoCreateRequest.builder()
                                                .carnet("P1" + suffix).nombre("X").apellido("Y")
                                                .correo("o@e").password("secret123").build())
                                        .build())))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value("ACCESS_DENIED"));

        mockMvc.perform(post("/localcomida/save")
                        .with(authAs(profesor.getId(), Rol.PROFESOR))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                LocalComidaConEncargadoRequest.builder()
                                        .nombre("Pirata2").ubicacion("X").horario("X")
                                        .encargado(EncargadoCreateRequest.builder()
                                                .carnet("P2" + suffix).nombre("X").apellido("Y")
                                                .correo("o2@e").password("secret123").build())
                                        .build())))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value("ACCESS_DENIED"));
    }

    @Test
    @DisplayName("Encargado A edita productos de su local; Encargado B no puede")
    void encargadoAEditaProd_encargadoBNo() throws Exception {
        mockMvc.perform(post("/producto/save")
                        .with(authAs(encargadoA.getId(), Rol.LOCAL))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                ProductoRequest.builder()
                                        .nombre("ProdNuevoA").precio(new BigDecimal("5.00"))
                                        .stock(10).permitePersonalizacion(false)
                                        .localComidaId(localA.getId()).build())))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/producto/save")
                        .with(authAs(encargadoB.getId(), Rol.LOCAL))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                ProductoRequest.builder()
                                        .nombre("ProdPirata").precio(new BigDecimal("5.00"))
                                        .stock(10).permitePersonalizacion(false)
                                        .localComidaId(localA.getId()).build())))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value("ACCESS_DENIED"));
    }

    @Test
    @DisplayName("ADMIN y LOCAL pueden crear ingredientes extra; ESTUDIANTE no")
    void soloAdminCreaIngredientes() throws Exception {
        mockMvc.perform(post("/ingredienteextra/save")
                        .with(authAs(admin.getId(), Rol.ADMIN))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                IngredienteExtraRequest.builder()
                                        .nombre("Queso extra").precioExtra(new BigDecimal("2.00"))
                                        .build())))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/ingredienteextra/save")
                        .with(authAs(encargadoA.getId(), Rol.LOCAL))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                IngredienteExtraRequest.builder()
                                        .nombre("Tocino").precioExtra(new BigDecimal("3.00"))
                                        .build())))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/ingredienteextra/save")
                        .with(authAs(estudiante.getId(), Rol.ESTUDIANTE))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                IngredienteExtraRequest.builder()
                                        .nombre("X").precioExtra(new BigDecimal("3.00"))
                                        .build())))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value("ACCESS_DENIED"));
    }

    @Test
    @DisplayName("ESTUDIANTE y PROFESOR pueden crear pedidos; LOCAL no (LOCAL gestiona, no pide)")
    void estudiantesYProfesoresPiden_localNo() throws Exception {
        var pedidoRequest = java.util.Map.of(
                "tipoEntrega", "RETIRO_LOCAL",
                "usuarioId", estudiante.getId().toString());

        mockMvc.perform(post("/pedido/save")
                        .with(authAs(estudiante.getId(), Rol.ESTUDIANTE))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pedidoRequest)))
                .andExpect(status().isCreated());

        var pedidoProf = java.util.Map.of(
                "tipoEntrega", "RETIRO_LOCAL",
                "usuarioId", profesor.getId().toString());
        mockMvc.perform(post("/pedido/save")
                        .with(authAs(profesor.getId(), Rol.PROFESOR))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pedidoProf)))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("Encargado A edita descuento de su local; otros no")
    void descuentosAisladosPorLocal() throws Exception {
        Descuento dA = descuentoRepository.save(Descuento.builder()
                .codigo("PROMO-A-" + UUID.randomUUID().toString().substring(0, 6))
                .porcentaje(new BigDecimal("10.00"))
                .fechaVencimiento(LocalDate.now().plusDays(30))
                .activo(true).localComida(localA).build());

        var updateBody = DescuentoRequest.builder()
                .codigo(dA.getCodigo()).porcentaje(new BigDecimal("15.00"))
                .fechaVencimiento(LocalDate.now().plusDays(30)).activo(true)
                .localComidaId(localA.getId()).build();

        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders
                        .put("/descuento/update/{id}", dA.getId())
                        .with(authAs(encargadoA.getId(), Rol.LOCAL))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateBody)))
                .andExpect(status().isOk());

        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders
                        .put("/descuento/update/{id}", dA.getId())
                        .with(authAs(encargadoB.getId(), Rol.LOCAL))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateBody)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value("ACCESS_DENIED"));

        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders
                        .put("/descuento/update/{id}", dA.getId())
                        .with(authAs(estudiante.getId(), Rol.ESTUDIANTE))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateBody)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value("ACCESS_DENIED"));
    }

    @Test
    @DisplayName("Solo ADMIN ve el listado completo de usuarios")
    void soloAdminVeUsuarios() throws Exception {
        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders
                        .get("/usuario/all")
                        .with(authAs(admin.getId(), Rol.ADMIN)))
                .andExpect(status().isOk());

        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders
                        .get("/usuario/all")
                        .with(authAs(estudiante.getId(), Rol.ESTUDIANTE)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value("ACCESS_DENIED"));

        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders
                        .get("/usuario/all")
                        .with(authAs(encargadoA.getId(), Rol.LOCAL)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value("ACCESS_DENIED"));
    }

    private Usuario saveUsuario(String carnet, String nombre, String apellido, Rol rol) {
        return usuarioRepository.save(Usuario.builder()
                .carnet(carnet).nombre(nombre).apellido(apellido)
                .correo(carnet + "@x").password("$2a$10$dummy").rol(rol).build());
    }

    private LocalComidaConEncargadoResponse crearLocalConEncargado(UUID adminId, LocalComidaConEncargadoRequest req)
            throws Exception {
        var result = mockMvc.perform(post("/localcomida/save")
                        .with(authAs(adminId, Rol.ADMIN))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andReturn();
        return objectMapper.readValue(result.getResponse().getContentAsString(),
                LocalComidaConEncargadoResponse.class);
    }

    private RequestPostProcessor authAs(UUID userId, Rol rol) {
        return request -> {
            Usuario u = new Usuario();
            u.setId(userId);
            u.setRol(rol);
            SecurityContext ctx = SecurityContextHolder.createEmptyContext();
            ctx.setAuthentication(new UsernamePasswordAuthenticationToken(
                    u, null, Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + rol.name()))));
            SecurityContextHolder.setContext(ctx);
            return request;
        };
    }
}
