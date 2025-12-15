package com.recetas.recetas.service;

import com.recetas.recetas.dto.ValoracionResponse;
import com.recetas.recetas.model.Receta;
import com.recetas.recetas.model.Usuario;
import com.recetas.recetas.model.Valoracion;
import com.recetas.recetas.repository.RecetaRepository;
import com.recetas.recetas.repository.UsuarioRepository;
import com.recetas.recetas.repository.ValoracionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ValoracionServiceTest {

    @Mock
    private ValoracionRepository valoracionRepository;

    @Mock
    private RecetaRepository recetaRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private ValoracionService valoracionService;

    private Receta receta;
    private Usuario usuario;
    private Valoracion valoracion;

    @BeforeEach
    void setUp() {
        receta = new Receta();
        receta.setId(1L);
        receta.setNombre("Receta Test");

        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setUsername("testuser");

        valoracion = new Valoracion();
        valoracion.setId(1L);
        valoracion.setReceta(receta);
        valoracion.setUsuario(usuario);
        valoracion.setPuntuacion(5);
        valoracion.setFechaCreacion(LocalDateTime.now());
    }

    @Test
    void testCrearOActualizarValoracion_NuevaValoracion() {
        when(recetaRepository.findById(1L)).thenReturn(Optional.of(receta));
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(valoracionRepository.findByRecetaIdAndUsuarioId(1L, 1L)).thenReturn(Optional.empty());
        when(valoracionRepository.save(any(Valoracion.class))).thenReturn(valoracion);

        Valoracion resultado = valoracionService.crearOActualizarValoracion(1L, 1L, 5);

        assertNotNull(resultado);
        assertEquals(5, resultado.getPuntuacion());
        verify(recetaRepository).findById(1L);
        verify(usuarioRepository).findById(1L);
        verify(valoracionRepository).findByRecetaIdAndUsuarioId(1L, 1L);
        verify(valoracionRepository).save(any(Valoracion.class));
    }

    @Test
    void testCrearOActualizarValoracion_ActualizarExistente() {
        when(recetaRepository.findById(1L)).thenReturn(Optional.of(receta));
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(valoracionRepository.findByRecetaIdAndUsuarioId(1L, 1L)).thenReturn(Optional.of(valoracion));
        when(valoracionRepository.save(any(Valoracion.class))).thenReturn(valoracion);

        Valoracion resultado = valoracionService.crearOActualizarValoracion(1L, 1L, 4);

        assertNotNull(resultado);
        assertEquals(4, resultado.getPuntuacion());
        verify(valoracionRepository).findByRecetaIdAndUsuarioId(1L, 1L);
        verify(valoracionRepository).save(valoracion);
    }

    @Test
    void testCrearOActualizarValoracion_RecetaNoEncontrada() {
        when(recetaRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            valoracionService.crearOActualizarValoracion(999L, 1L, 5);
        });

        verify(recetaRepository).findById(999L);
        verify(usuarioRepository, never()).findById(any());
    }

    @Test
    void testCrearOActualizarValoracion_UsuarioNoEncontrado() {
        when(recetaRepository.findById(1L)).thenReturn(Optional.of(receta));
        when(usuarioRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            valoracionService.crearOActualizarValoracion(1L, 999L, 5);
        });

        verify(recetaRepository).findById(1L);
        verify(usuarioRepository).findById(999L);
    }

    @Test
    void testObtenerValoracionesPorReceta_ConUsuario() {
        when(valoracionRepository.calcularPromedioPorRecetaId(1L)).thenReturn(4.5);
        when(valoracionRepository.contarPorRecetaId(1L)).thenReturn(10L);
        when(valoracionRepository.findByRecetaIdAndUsuarioId(1L, 1L)).thenReturn(Optional.of(valoracion));

        ValoracionResponse resultado = valoracionService.obtenerValoracionesPorReceta(1L, 1L);

        assertNotNull(resultado);
        assertEquals(4.5, resultado.getPromedio());
        assertEquals(10L, resultado.getTotalValoraciones());
        assertEquals(5, resultado.getMiValoracion());
        verify(valoracionRepository).calcularPromedioPorRecetaId(1L);
        verify(valoracionRepository).contarPorRecetaId(1L);
        verify(valoracionRepository).findByRecetaIdAndUsuarioId(1L, 1L);
    }

    @Test
    void testObtenerValoracionesPorReceta_SinUsuario() {
        when(valoracionRepository.calcularPromedioPorRecetaId(1L)).thenReturn(4.5);
        when(valoracionRepository.contarPorRecetaId(1L)).thenReturn(10L);

        ValoracionResponse resultado = valoracionService.obtenerValoracionesPorReceta(1L, null);

        assertNotNull(resultado);
        assertEquals(4.5, resultado.getPromedio());
        assertEquals(10L, resultado.getTotalValoraciones());
        assertNull(resultado.getMiValoracion());
        verify(valoracionRepository, never()).findByRecetaIdAndUsuarioId(any(), any());
    }

    @Test
    void testObtenerValoracionesPorReceta_SinPromedio() {
        when(valoracionRepository.calcularPromedioPorRecetaId(1L)).thenReturn(null);
        when(valoracionRepository.contarPorRecetaId(1L)).thenReturn(null);

        ValoracionResponse resultado = valoracionService.obtenerValoracionesPorReceta(1L, null);

        assertNotNull(resultado);
        assertEquals(0.0, resultado.getPromedio());
        assertEquals(0L, resultado.getTotalValoraciones());
    }

    @Test
    void testObtenerValoracionesPorReceta_UsuarioSinValoracion() {
        when(valoracionRepository.calcularPromedioPorRecetaId(1L)).thenReturn(4.5);
        when(valoracionRepository.contarPorRecetaId(1L)).thenReturn(10L);
        when(valoracionRepository.findByRecetaIdAndUsuarioId(1L, 1L)).thenReturn(Optional.empty());

        ValoracionResponse resultado = valoracionService.obtenerValoracionesPorReceta(1L, 1L);

        assertNotNull(resultado);
        assertNull(resultado.getMiValoracion());
    }

    @Test
    void testObtenerValoracionesPorReceta_SinValoraciones() {
        when(valoracionRepository.calcularPromedioPorRecetaId(1L)).thenReturn(null);
        when(valoracionRepository.contarPorRecetaId(1L)).thenReturn(0L);

        ValoracionResponse resultado = valoracionService.obtenerValoracionesPorReceta(1L, null);

        assertNotNull(resultado);
        assertEquals(0.0, resultado.getPromedio());
        assertEquals(0L, resultado.getTotalValoraciones());
        assertNull(resultado.getMiValoracion());
    }
}

