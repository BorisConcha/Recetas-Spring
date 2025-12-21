package com.recetas.recetas.util;

import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SecurityUtilTest {

    @Test
    void testGetCurrentUsername_ConUsuarioAutenticado() {
        UserDetails userDetails = User.builder()
                .username("testuser")
                .password("password")
                .authorities(new ArrayList<>())
                .build();

        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(userDetails);

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);

        SecurityContextHolder.setContext(securityContext);

        String username = SecurityUtil.getCurrentUsername();

        assertEquals("testuser", username);
    }

    @Test
    void testGetCurrentUsername_SinAutenticacion() {
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(null);

        SecurityContextHolder.setContext(securityContext);

        String username = SecurityUtil.getCurrentUsername();

        assertNull(username);
    }

    @Test
    void testGetCurrentUsername_PrincipalNoEsUserDetails() {
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn("anonymousUser");

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);

        SecurityContextHolder.setContext(securityContext);

        String username = SecurityUtil.getCurrentUsername();

        assertNull(username);
    }
    
    @Test
    void testGetCurrentUsername_SecurityContextNull() {
        SecurityContextHolder.clearContext();
        
        String username = SecurityUtil.getCurrentUsername();
        
        // Cuando no hay contexto de seguridad, getAuthentication() retorna null
        assertNull(username);
    }
    
    @Test
    void testSecurityUtilConstructor() {
        // Instanciar la clase para cubrir el constructor por defecto
        SecurityUtil util = new SecurityUtil();
        assertNotNull(util);
    }
}

