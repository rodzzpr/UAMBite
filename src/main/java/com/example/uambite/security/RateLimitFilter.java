package com.example.uambite.security;

import com.example.uambite.exceptions.BusinessException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RateLimitFilter extends OncePerRequestFilter {

    private final ConcurrentHashMap<String, Deque<Long>> attemptsByIp = new ConcurrentHashMap<>();
    private final int maxAttempts;
    private final long windowMs;

    public RateLimitFilter(@Value("${app.ratelimit.login.max-attempts}") int maxAttempts,
                           @Value("${app.ratelimit.login.window-ms}") long windowMs) {
        this.maxAttempts = maxAttempts;
        this.windowMs = windowMs;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        if ("POST".equalsIgnoreCase(request.getMethod()) && "/auth/login".equals(request.getRequestURI())) {
            String key = clientIp(request);
            long now = System.currentTimeMillis();
            Deque<Long> attempts = attemptsByIp.computeIfAbsent(key, k -> new ArrayDeque<>());
            synchronized (attempts) {
                while (!attempts.isEmpty() && now - attempts.peekFirst() > windowMs) {
                    attempts.pollFirst();
                }
                if (attempts.size() >= maxAttempts) {
                    long retryAfterMs = windowMs - (now - attempts.peekFirst());
                    response.setHeader("Retry-After", String.valueOf(Math.max(1, retryAfterMs / 1000)));
                    throw new BusinessException(
                            "Demasiados intentos de login. Intente nuevamente en unos segundos.",
                            HttpStatus.TOO_MANY_REQUESTS, "RATE_LIMIT");
                }
                attempts.addLast(now);
            }
        }
        filterChain.doFilter(request, response);
    }

    private String clientIp(HttpServletRequest request) {
        String xff = request.getHeader("X-Forwarded-For");
        if (xff != null && !xff.isBlank()) {
            return xff.split(",")[0].trim();
        }
        return request.getRemoteAddr() == null ? "unknown" : request.getRemoteAddr();
    }
}
