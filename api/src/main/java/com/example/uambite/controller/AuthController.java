package com.example.uambite.controller;

import com.example.uambite.dto.request.CambiarPasswordRequest;
import com.example.uambite.dto.request.LoginRequest;
import com.example.uambite.dto.request.RegisterRequest;
import com.example.uambite.dto.response.AuthResponse;
import com.example.uambite.dto.response.UsuarioResponse;
import com.example.uambite.model.Usuario;
import com.example.uambite.security.JwtUtil;
import com.example.uambite.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticación", description = "Registro y login de usuarios")
public class AuthController {

    private final UsuarioService usuarioService;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @PostMapping("/register")
    @Operation(summary = "Registrar un nuevo usuario")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        UsuarioResponse response = usuarioService.register(
                request.getCarnet(),
                request.getNombre(),
                request.getApellido(),
                request.getPassword(),
                request.getRol(),
                request.getCorreo());
        String token = jwtUtil.generateToken(response.getCarnet(), response.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(new AuthResponse(
                token, response.getId(), response.getCarnet(), response.getNombre(),
                response.getApellido(), response.getCorreo(), response.getRol(), false));
    }

    @PostMapping("/login")
    @Operation(summary = "Iniciar sesión")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        Usuario usuario = usuarioService.findByCarnet(request.getCarnet())
                .orElseThrow(() -> new com.example.uambite.exceptions.BusinessException(
                        "Carnet o contraseña incorrectos."));

        if (!passwordEncoder.matches(request.getPassword(), usuario.getPassword())) {
            throw new com.example.uambite.exceptions.BusinessException(
                    "Carnet o contraseña incorrectos.");
        }

        String token = jwtUtil.generateToken(usuario.getCarnet(), usuario.getId());
        return ResponseEntity.ok(new AuthResponse(
                token, usuario.getId(), usuario.getCarnet(),
                usuario.getNombre(), usuario.getApellido(), usuario.getCorreo(),
                usuario.getRol(), Boolean.TRUE.equals(usuario.getPasswordTemporal())));
    }

    @PostMapping("/cambiar-password")
    @Operation(summary = "Cambiar la contraseña del usuario autenticado. "
            + "Si el usuario tiene contraseña temporal, la marca como definitiva.")
    public ResponseEntity<Map<String, String>> cambiarPassword(
            @AuthenticationPrincipal(expression = "id") UUID usuarioId,
            @Valid @RequestBody CambiarPasswordRequest request) {
        if (usuarioId == null) {
            throw new com.example.uambite.exceptions.BusinessException(
                    "No autenticado.", HttpStatus.UNAUTHORIZED, "UNAUTHORIZED");
        }
        usuarioService.cambiarPassword(usuarioId,
                request.getPasswordActual(), request.getPasswordNuevo());
        return ResponseEntity.ok(Map.of("message", "Contraseña actualizada correctamente."));
    }
}
