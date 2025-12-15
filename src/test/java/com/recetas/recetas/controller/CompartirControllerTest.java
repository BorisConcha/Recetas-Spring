package com.recetas.recetas.controller;

import com.recetas.recetas.dto.CompartirRequest;
import com.recetas.recetas.model.RecetaCompartida;
import com.recetas.recetas.model.Usuario;
import com.recetas.recetas.repository.UsuarioRepository;
import com.recetas.recetas.service.CompartirService;
import com.recetas.recetas.util.SecurityUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CompartirControllerTest {

    @Mock
    private CompartirService compartirService;

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private CompartirController compartirController;

    private Usuario usuario;
    private CompartirRequest compartirRequest;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(compartirController, "baseUrl", "http://localhost:8080");

        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setUsername("testuser");

        compartirRequest = new CompartirRequest();
        compartirRequest.setRecetaId(1L);
        compartirRequest.setPlataforma("Facebook");
    }

    @Test
    void testCompartirReceta_Exitoso() {
        try (MockedStatic<SecurityUtil> mockedSecurityUtil = mockStatic(SecurityUtil.class)) {
            mockedSecurityUtil.when(SecurityUtil::getCurrentUsername).thenReturn("testuser");
            when(usuarioRepository.findByUsername("testuser")).thenReturn(Optional.of(usuario));
            when(compartirService.registrarCompartido(any(), any(), any())).thenReturn(new RecetaCompartida());

            ResponseEntity<?> response = compartirController.compartirReceta(compartirRequest);

            assertEquals(HttpStatus.OK, response.getStatusCode());
            verify(usuarioRepository).findByUsername("testuser");
            verify(compartirService).registrarCompartido(1L, 1L, "Facebook");
        }
    }

    @Test
    void testCompartirReceta_UsuarioNoAutenticado() {
        try (MockedStatic<SecurityUtil> mockedSecurityUtil = mockStatic(SecurityUtil.class)) {
            mockedSecurityUtil.when(SecurityUtil::getCurrentUsername).thenReturn(null);

            ResponseEntity<?> response = compartirController.compartirReceta(compartirRequest);

            assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
            verify(compartirService, never()).registrarCompartido(any(), any(), any());
        }
    }

    @Test
    void testCompartirReceta_Error() {
        try (MockedStatic<SecurityUtil> mockedSecurityUtil = mockStatic(SecurityUtil.class)) {
            mockedSecurityUtil.when(SecurityUtil::getCurrentUsername).thenReturn("testuser");
            when(usuarioRepository.findByUsername("testuser")).thenReturn(Optional.of(usuario));
            when(compartirService.registrarCompartido(any(), any(), any()))
                    .thenThrow(new RuntimeException("Error"));

            ResponseEntity<?> response = compartirController.compartirReceta(compartirRequest);

            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        }
    }

    @Test
    void testObtenerLinkCompartir() {
        when(compartirService.generarLinkCompartir(1L, "http://localhost:8080"))
                .thenReturn("http://localhost:8080/recetas/1");

        ResponseEntity<?> response = compartirController.obtenerLinkCompartir(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        if (response.getBody() instanceof java.util.Map) {
            @SuppressWarnings("unchecked")
            java.util.Map<String, String> body = (java.util.Map<String, String>) response.getBody();
            assertTrue(body.containsKey("link"));
            assertTrue(body.containsKey("facebook"));
            assertTrue(body.containsKey("twitter"));
            assertTrue(body.containsKey("whatsapp"));
        }
        verify(compartirService).generarLinkCompartir(1L, "http://localhost:8080");
    }

    @Test
    void testCompartirReceta_UsuarioNoEncontrado() {
        try (MockedStatic<SecurityUtil> mockedSecurityUtil = mockStatic(SecurityUtil.class)) {
            mockedSecurityUtil.when(SecurityUtil::getCurrentUsername).thenReturn("testuser");
            when(usuarioRepository.findByUsername("testuser")).thenReturn(Optional.empty());

            var response = compartirController.compartirReceta(compartirRequest);

            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        }
    }
}

