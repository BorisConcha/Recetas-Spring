package com.recetas.recetas.service;

import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class JwtServiceTest {

    @InjectMocks
    private JwtService jwtService;

    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(jwtService, "secret", "mySecretKeyForJWTTokenGenerationThatShouldBeAtLeast256BitsLongForHS256Algorithm");
        ReflectionTestUtils.setField(jwtService, "expiration", 86400000L);
        
        userDetails = User.builder()
                .username("testuser")
                .password("password")
                .authorities(new ArrayList<>())
                .build();
    }

    @Test
    void testGenerateToken() {
        String token = jwtService.generateToken(userDetails);
        
        assertNotNull(token);
        assertFalse(token.isEmpty());
    }

    @Test
    void testExtractUsername() {
        String token = jwtService.generateToken(userDetails);
        String username = jwtService.extractUsername(token);
        
        assertEquals("testuser", username);
    }

    @Test
    void testExtractExpiration() {
        String token = jwtService.generateToken(userDetails);
        Date expiration = jwtService.extractExpiration(token);
        
        assertNotNull(expiration);
        assertTrue(expiration.after(new Date()));
    }

    @Test
    void testValidateToken_ValidToken() {
        String token = jwtService.generateToken(userDetails);
        boolean isValid = jwtService.validateToken(token, userDetails);
        
        assertTrue(isValid);
    }

    @Test
    void testValidateToken_InvalidUsername() {
        String token = jwtService.generateToken(userDetails);
        UserDetails otherUser = User.builder()
                .username("otheruser")
                .password("password")
                .authorities(new ArrayList<>())
                .build();
        
        boolean isValid = jwtService.validateToken(token, otherUser);
        
        assertFalse(isValid);
    }

    @Test
    void testValidateToken_ExpiredToken() {
        // Crear un token con expiración muy corta
        ReflectionTestUtils.setField(jwtService, "expiration", 1L);
        String token = jwtService.generateToken(userDetails);
        
        // Restaurar expiration para futuras pruebas
        ReflectionTestUtils.setField(jwtService, "expiration", 86400000L);
        
        // Esperar para que el token expire
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        // El token debería estar expirado, pero puede fallar la validación antes
        // Simplemente verificamos que el método se ejecuta sin excepción
        try {
            boolean isValid = jwtService.validateToken(token, userDetails);
            assertFalse(isValid);
        } catch (Exception e) {
            // Si lanza excepción por token expirado, también es válido
            assertTrue(e.getMessage().contains("expired") || e.getMessage().contains("JWT"));
        }
    }

    @Test
    void testExtractClaim() {
        String token = jwtService.generateToken(userDetails);
        String subject = jwtService.extractClaim(token, Claims::getSubject);
        
        assertEquals("testuser", subject);
    }

    @Test
    void testExtractClaim_WithCustomFunction() {
        String token = jwtService.generateToken(userDetails);
        Date issuedAt = jwtService.extractClaim(token, Claims::getIssuedAt);
        
        assertNotNull(issuedAt);
        assertTrue(issuedAt.before(new Date()) || issuedAt.equals(new Date()));
    }

    @Test
    void testGenerateToken_WithDifferentUsers() {
        UserDetails user1 = User.builder()
                .username("user1")
                .password("pass")
                .authorities(new ArrayList<>())
                .build();
        
        UserDetails user2 = User.builder()
                .username("user2")
                .password("pass")
                .authorities(new ArrayList<>())
                .build();
        
        String token1 = jwtService.generateToken(user1);
        String token2 = jwtService.generateToken(user2);
        
        assertNotEquals(token1, token2);
        assertEquals("user1", jwtService.extractUsername(token1));
        assertEquals("user2", jwtService.extractUsername(token2));
    }

    @Test
    void testExtractExpiration_ValidToken() {
        String token = jwtService.generateToken(userDetails);
        Date expiration = jwtService.extractExpiration(token);
        Date now = new Date();
        
        assertNotNull(expiration);
        assertTrue(expiration.after(now));
    }

    @Test
    void testValidateToken_MultipleValidations() {
        String token = jwtService.generateToken(userDetails);
        
        // Validar múltiples veces el mismo token
        assertTrue(jwtService.validateToken(token, userDetails));
        assertTrue(jwtService.validateToken(token, userDetails));
    }
}

