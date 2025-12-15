package com.recetas.recetas.config;

import com.recetas.recetas.service.DetalleUserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SecurityConfigTest {

    @Mock
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Mock
    private DetalleUserService userDetailsService;

    private SecurityConfig securityConfig;

    @BeforeEach
    void setUp() {
        securityConfig = new SecurityConfig();
        ReflectionTestUtils.setField(securityConfig, "jwtAuthenticationFilter", jwtAuthenticationFilter);
        ReflectionTestUtils.setField(securityConfig, "userDetailsService", userDetailsService);
    }

    @Test
    void testPasswordEncoderBean() {
        PasswordEncoder passwordEncoder = securityConfig.passwordEncoder();
        
        assertNotNull(passwordEncoder);
        assertTrue(passwordEncoder instanceof BCryptPasswordEncoder);
        
        String encoded = passwordEncoder.encode("test");
        assertNotNull(encoded);
        assertTrue(passwordEncoder.matches("test", encoded));
        assertFalse(passwordEncoder.matches("wrong", encoded));
    }

    @Test
    void testPasswordEncoderBean_MultiplePasswords() {
        PasswordEncoder passwordEncoder = securityConfig.passwordEncoder();
        
        String[] passwords = {"admin123", "chef123", "usuario123"};
        for (String password : passwords) {
            String encoded = passwordEncoder.encode(password);
            assertNotNull(encoded);
            assertTrue(passwordEncoder.matches(password, encoded));
        }
    }

    @Test
    void testAuthenticationManagerBean() throws Exception {
        AuthenticationConfiguration config = mock(AuthenticationConfiguration.class);
        AuthenticationManager authManager = mock(AuthenticationManager.class);
        
        when(config.getAuthenticationManager()).thenReturn(authManager);
        
        AuthenticationManager result = securityConfig.authenticationManager(config);
        
        assertNotNull(result);
        assertEquals(authManager, result);
        verify(config).getAuthenticationManager();
    }

    @Test
    void testAuthenticationProviderBean() {
        AuthenticationProvider provider = securityConfig.authenticationProvider();
        
        assertNotNull(provider);
        assertTrue(provider instanceof DaoAuthenticationProvider);
    }

    @Test
    void testSecurityConfigInstantiation() {
        SecurityConfig config = new SecurityConfig();
        assertNotNull(config);
    }

    @Test
    void testPasswordEncoderBean_DifferentStrengths() {
        PasswordEncoder encoder1 = securityConfig.passwordEncoder();
        PasswordEncoder encoder2 = securityConfig.passwordEncoder();
        
        // Verificar que cada llamada retorna una nueva instancia o la misma
        assertNotNull(encoder1);
        assertNotNull(encoder2);
        
        // Verificar que ambos funcionan correctamente
        String password = "test123";
        String hash1 = encoder1.encode(password);
        String hash2 = encoder2.encode(password);
        
        assertTrue(encoder1.matches(password, hash1));
        assertTrue(encoder2.matches(password, hash2));
    }
}
