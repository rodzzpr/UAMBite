package com.example.uambite;

import com.example.uambite.exceptions.BusinessException;
import com.example.uambite.security.RateLimitFilter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class RateLimitFilterTest {

    private RateLimitFilter filter;

    @BeforeEach
    void setUp() {
        filter = new RateLimitFilter(3, 60_000L);
    }

    @Test
    void permiteHastaNIntentosYLuegoBloquea() throws ServletException, IOException {
        for (int i = 0; i < 3; i++) {
            assertDoesNotThrow(() -> doFilter("POST", "/auth/login", "1.1.1.1"));
        }
        BusinessException ex = assertThrows(BusinessException.class,
                () -> doFilter("POST", "/auth/login", "1.1.1.1"));
        assertEquals("RATE_LIMIT", ex.getCode());
    }

    @Test
    void ipsDistintasTienenBucketsIndependientes() throws ServletException, IOException {
        for (int i = 0; i < 3; i++) {
            doFilter("POST", "/auth/login", "1.1.1.1");
        }
        assertDoesNotThrow(() -> doFilter("POST", "/auth/login", "2.2.2.2"));
    }

    @Test
    void soloAplicaAPostAuthLogin() throws ServletException, IOException {
        for (int i = 0; i < 10; i++) {
            assertDoesNotThrow(() -> doFilter("GET", "/localcomida/all", "1.1.1.1"));
        }
    }

    @Test
    void respetaHeaderXForwardedFor() throws ServletException, IOException {
        for (int i = 0; i < 3; i++) {
            doFilterXff("POST", "/auth/login", "10.0.0.1, 1.1.1.1");
        }
        assertThrows(BusinessException.class,
                () -> doFilterXff("POST", "/auth/login", "10.0.0.1, 1.1.1.1"));
        assertDoesNotThrow(() -> doFilterXff("POST", "/auth/login", "10.0.0.2, 2.2.2.2"));
    }

    private void doFilter(String method, String uri, String remoteAddr) throws ServletException, IOException {
        MockHttpServletRequest req = new MockHttpServletRequest(method, uri);
        req.setRemoteAddr(remoteAddr);
        MockHttpServletResponse res = new MockHttpServletResponse();
        FilterChain chain = new MockFilterChain();
        try {
            filter.doFilter(req, res, chain);
        } catch (BusinessException e) {
            throw e;
        }
    }

    private void doFilterXff(String method, String uri, String xff) throws ServletException, IOException {
        MockHttpServletRequest req = new MockHttpServletRequest(method, uri);
        req.setRemoteAddr("127.0.0.1");
        req.addHeader("X-Forwarded-For", xff);
        MockHttpServletResponse res = new MockHttpServletResponse();
        FilterChain chain = new MockFilterChain();
        filter.doFilter(req, res, chain);
    }
}
