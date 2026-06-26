package com.example.uambite.security;

import com.example.uambite.model.Usuario;
import com.example.uambite.service.UsuarioService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.Optional;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UsuarioService usuarioService;

    public JwtAuthenticationFilter(JwtUtil jwtUtil, UsuarioService usuarioService) {
        this.jwtUtil = jwtUtil;
        this.usuarioService = usuarioService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);
            try {
                Claims claims = jwtUtil.validateToken(token);
                String carnet = claims.getSubject();
                Optional<Usuario> usuarioOpt = usuarioService.findByCarnet(carnet);
                if (usuarioOpt.isPresent()) {
                    Usuario usuario = usuarioOpt.get();
                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(
                                    usuario, null,
                                    Collections.singletonList(
                                            new SimpleGrantedAuthority("ROLE_" + usuario.getRol().toUpperCase())
                                    )
                            );
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            } catch (Exception e) {
                // Token invalido o expirado, se ignora
            }
        }
        filterChain.doFilter(request, response);
    }
}
