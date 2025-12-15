package com.recetas.recetas.controller;

import com.recetas.recetas.dto.ValoracionRequest;
import com.recetas.recetas.dto.ValoracionResponse;
import com.recetas.recetas.model.Usuario;
import com.recetas.recetas.model.Valoracion;
import com.recetas.recetas.repository.UsuarioRepository;
import com.recetas.recetas.service.ValoracionService;
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

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ValoracionControllerTest {

    @Mock
    private ValoracionService valoracionService;

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private ValoracionController valoracionController;

    private Usuario usuario;
    private ValoracionRequest valoracionRequest;
    private ValoracionResponse valoracionResponse;

    @BeforeEach
    void setUp() {
        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setUsername("testuser");

        valoracionRequest = new ValoracionRequest();
        valoracionRequest.setRecetaId(1L);
        valoracionRequest.setPuntuacion(5);

        valoracionResponse = new ValoracionResponse(4.5, 10L, 5);
    }

    @Test
    void testCrearOActualizarValoracion_Exitoso() {
        try (MockedStatic<SecurityUtil> mockedSecurityUtil = mockStatic(SecurityUtil.class)) {
            mockedSecurityUtil.when(SecurityUtil::getCurrentUsername).thenReturn("testuser");
            when(usuarioRepository.findByUsername("testuser")).thenReturn(Optional.of(usuario));
            when(valoracionService.crearOActualizarValoracion(any(), any(), any())).thenReturn(new Valoracion());

            ResponseEntity<?> response = valoracionController.crearOActualizarValoracion(valoracionRequest);

            assertEquals(HttpStatus.OK, response.getStatusCode());
            verify(usuarioRepository).findByUsername("testuser");
            verify(valoracionService).crearOActualizarValoracion(1L, 1L, 5);
        }
    }

    @Test
    void testCrearOActualizarValoracion_UsuarioNoAutenticado() {
        try (MockedStatic<SecurityUtil> mockedSecurityUtil = mockStatic(SecurityUtil.class)) {
            mockedSecurityUtil.when(SecurityUtil::getCurrentUsername).thenReturn(null);

            ResponseEntity<?> response = valoracionController.crearOActualizarValoracion(valoracionRequest);

            assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
            verify(valoracionService, never()).crearOActualizarValoracion(any(), any(), any());
        }
    }

    @Test
    void testCrearOActualizarValoracion_Error() {
        try (MockedStatic<SecurityUtil> mockedSecurityUtil = mockStatic(SecurityUtil.class)) {
            mockedSecurityUtil.when(SecurityUtil::getCurrentUsername).thenReturn("testuser");
            when(usuarioRepository.findByUsername("testuser")).thenReturn(Optional.of(usuario));
            when(valoracionService.crearOActualizarValoracion(any(), any(), any()))
                    .thenThrow(new RuntimeException("Error"));

            ResponseEntity<?> response = valoracionController.crearOActualizarValoracion(valoracionRequest);

            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        }
    }

    @Test
    void testObtenerValoraciones_ConUsuario() {
        try (MockedStatic<SecurityUtil> mockedSecurityUtil = mockStatic(SecurityUtil.class)) {
            mockedSecurityUtil.when(SecurityUtil::getCurrentUsername).thenReturn("testuser");
            when(usuarioRepository.findByUsername("testuser")).thenReturn(Optional.of(usuario));
            when(valoracionService.obtenerValoracionesPorReceta(1L, 1L)).thenReturn(valoracionResponse);

            ResponseEntity<ValoracionResponse> response = valoracionController.obtenerValoraciones(1L);

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());
            assertEquals(4.5, response.getBody().getPromedio());
            verify(valoracionService).obtenerValoracionesPorReceta(1L, 1L);
        }
    }

    @Test
    void testObtenerValoraciones_SinUsuario() {
        try (MockedStatic<SecurityUtil> mockedSecurityUtil = mockStatic(SecurityUtil.class)) {
            mockedSecurityUtil.when(SecurityUtil::getCurrentUsername).thenReturn(null);
            when(valoracionService.obtenerValoracionesPorReceta(1L, null)).thenReturn(valoracionResponse);

            ResponseEntity<ValoracionResponse> response = valoracionController.obtenerValoraciones(1L);

            assertEquals(HttpStatus.OK, response.getStatusCode());
            verify(valoracionService).obtenerValoracionesPorReceta(1L, null);
        }
    }

    @Test
    void testObtenerValoraciones_Error() {
        try (MockedStatic<SecurityUtil> mockedSecurityUtil = mockStatic(SecurityUtil.class)) {
            mockedSecurityUtil.when(SecurityUtil::getCurrentUsername).thenReturn("testuser");
            when(usuarioRepository.findByUsername("testuser")).thenReturn(Optional.of(usuario));
            when(valoracionService.obtenerValoracionesPorReceta(1L, 1L))
                    .thenThrow(new RuntimeException("Error"));

            ResponseEntity<ValoracionResponse> response = valoracionController.obtenerValoraciones(1L);

            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        }
    }

    @Test
    void testObtenerValoraciones_UsuarioNull() {
        try (MockedStatic<SecurityUtil> mockedSecurityUtil = mockStatic(SecurityUtil.class)) {
            mockedSecurityUtil.when(SecurityUtil::getCurrentUsername).thenReturn("testuser");
            when(usuarioRepository.findByUsername("testuser")).thenReturn(Optional.empty());
            when(valoracionService.obtenerValoracionesPorReceta(1L, null)).thenReturn(valoracionResponse);

            ResponseEntity<ValoracionResponse> response = valoracionController.obtenerValoraciones(1L);

            assertEquals(HttpStatus.OK, response.getStatusCode());
            verify(valoracionService).obtenerValoracionesPorReceta(1L, null);
        }
    }
}

