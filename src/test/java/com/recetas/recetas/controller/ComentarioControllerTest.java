package com.recetas.recetas.controller;

import com.recetas.recetas.dto.ComentarioRequest;
import com.recetas.recetas.dto.ComentarioResponse;
import com.recetas.recetas.model.Usuario;
import com.recetas.recetas.repository.UsuarioRepository;
import com.recetas.recetas.service.ComentarioService;
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

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ComentarioControllerTest {

    @Mock
    private ComentarioService comentarioService;

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private ComentarioController comentarioController;

    private Usuario usuario;
    private ComentarioRequest comentarioRequest;
    private ComentarioResponse comentarioResponse;

    @BeforeEach
    void setUp() {
        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setUsername("testuser");

        comentarioRequest = new ComentarioRequest();
        comentarioRequest.setRecetaId(1L);
        comentarioRequest.setComentario("Comentario de prueba");

        comentarioResponse = new ComentarioResponse(
                1L, 1L, "Test User", "Comentario de prueba", LocalDateTime.now()
        );
    }

    @Test
    void testCrearComentario_Exitoso() {
        try (MockedStatic<SecurityUtil> mockedSecurityUtil = mockStatic(SecurityUtil.class)) {
            mockedSecurityUtil.when(SecurityUtil::getCurrentUsername).thenReturn("testuser");
            when(usuarioRepository.findByUsername("testuser")).thenReturn(Optional.of(usuario));
            when(comentarioService.crearComentario(any(), any(), any())).thenReturn(new com.recetas.recetas.model.Comentario());

            ResponseEntity<?> response = comentarioController.crearComentario(comentarioRequest);

            assertEquals(HttpStatus.CREATED, response.getStatusCode());
            verify(usuarioRepository).findByUsername("testuser");
            verify(comentarioService).crearComentario(1L, 1L, "Comentario de prueba");
        }
    }

    @Test
    void testCrearComentario_UsuarioNoAutenticado() {
        try (MockedStatic<SecurityUtil> mockedSecurityUtil = mockStatic(SecurityUtil.class)) {
            mockedSecurityUtil.when(SecurityUtil::getCurrentUsername).thenReturn(null);

            ResponseEntity<?> response = comentarioController.crearComentario(comentarioRequest);

            assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
            verify(comentarioService, never()).crearComentario(any(), any(), any());
        }
    }

    @Test
    void testCrearComentario_Error() {
        try (MockedStatic<SecurityUtil> mockedSecurityUtil = mockStatic(SecurityUtil.class)) {
            mockedSecurityUtil.when(SecurityUtil::getCurrentUsername).thenReturn("testuser");
            when(usuarioRepository.findByUsername("testuser")).thenReturn(Optional.of(usuario));
            doThrow(new RuntimeException("Error")).when(comentarioService).crearComentario(any(), any(), any());

            ResponseEntity<?> response = comentarioController.crearComentario(comentarioRequest);

            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        }
    }

    @Test
    void testObtenerComentarios() {
        List<ComentarioResponse> comentarios = Arrays.asList(comentarioResponse);
        when(comentarioService.obtenerComentariosPorReceta(1L)).thenReturn(comentarios);

        ResponseEntity<List<ComentarioResponse>> response = comentarioController.obtenerComentarios(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        verify(comentarioService).obtenerComentariosPorReceta(1L);
    }

    @Test
    void testEliminarComentario_Exitoso() {
        try (MockedStatic<SecurityUtil> mockedSecurityUtil = mockStatic(SecurityUtil.class)) {
            mockedSecurityUtil.when(SecurityUtil::getCurrentUsername).thenReturn("testuser");
            when(usuarioRepository.findByUsername("testuser")).thenReturn(Optional.of(usuario));
            doNothing().when(comentarioService).eliminarComentario(1L, 1L);

            ResponseEntity<?> response = comentarioController.eliminarComentario(1L);

            assertEquals(HttpStatus.OK, response.getStatusCode());
            verify(comentarioService).eliminarComentario(1L, 1L);
        }
    }

    @Test
    void testEliminarComentario_UsuarioNoAutenticado() {
        try (MockedStatic<SecurityUtil> mockedSecurityUtil = mockStatic(SecurityUtil.class)) {
            mockedSecurityUtil.when(SecurityUtil::getCurrentUsername).thenReturn(null);

            ResponseEntity<?> response = comentarioController.eliminarComentario(1L);

            assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
            verify(comentarioService, never()).eliminarComentario(any(), any());
        }
    }

    @Test
    void testEliminarComentario_Error() {
        try (MockedStatic<SecurityUtil> mockedSecurityUtil = mockStatic(SecurityUtil.class)) {
            mockedSecurityUtil.when(SecurityUtil::getCurrentUsername).thenReturn("testuser");
            when(usuarioRepository.findByUsername("testuser")).thenReturn(Optional.of(usuario));
            doThrow(new RuntimeException("Error")).when(comentarioService).eliminarComentario(1L, 1L);

            ResponseEntity<?> response = comentarioController.eliminarComentario(1L);

            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        }
    }

    @Test
    void testCrearComentario_UsuarioNoEncontrado() {
        try (MockedStatic<SecurityUtil> mockedSecurityUtil = mockStatic(SecurityUtil.class)) {
            mockedSecurityUtil.when(SecurityUtil::getCurrentUsername).thenReturn("testuser");
            when(usuarioRepository.findByUsername("testuser")).thenReturn(Optional.empty());

            ResponseEntity<?> response = comentarioController.crearComentario(comentarioRequest);

            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        }
    }

    @Test
    void testEliminarComentario_UsuarioNoEncontrado() {
        try (MockedStatic<SecurityUtil> mockedSecurityUtil = mockStatic(SecurityUtil.class)) {
            mockedSecurityUtil.when(SecurityUtil::getCurrentUsername).thenReturn("testuser");
            when(usuarioRepository.findByUsername("testuser")).thenReturn(Optional.empty());

            ResponseEntity<?> response = comentarioController.eliminarComentario(1L);

            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        }
    }
}

