package com.recetas.recetas.service;

import com.recetas.recetas.dto.ComentarioResponse;
import com.recetas.recetas.model.Comentario;
import com.recetas.recetas.model.Receta;
import com.recetas.recetas.model.Usuario;
import com.recetas.recetas.repository.ComentarioRepository;
import com.recetas.recetas.repository.RecetaRepository;
import com.recetas.recetas.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ComentarioServiceTest {

    @Mock
    private ComentarioRepository comentarioRepository;

    @Mock
    private RecetaRepository recetaRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private ComentarioService comentarioService;

    private Receta receta;
    private Usuario usuario;
    private Comentario comentario;

    @BeforeEach
    void setUp() {
        receta = new Receta();
        receta.setId(1L);
        receta.setNombre("Receta Test");

        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setUsername("testuser");
        usuario.setNombreCompleto("Test User");

        comentario = new Comentario();
        comentario.setId(1L);
        comentario.setReceta(receta);
        comentario.setUsuario(usuario);
        comentario.setComentario("Comentario de prueba");
        comentario.setFechaCreacion(LocalDateTime.now());
    }

    @Test
    void testCrearComentario() {
        when(recetaRepository.findById(1L)).thenReturn(Optional.of(receta));
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(comentarioRepository.save(any(Comentario.class))).thenReturn(comentario);

        Comentario resultado = comentarioService.crearComentario(1L, 1L, "Comentario de prueba");

        assertNotNull(resultado);
        assertEquals("Comentario de prueba", resultado.getComentario());
        verify(recetaRepository).findById(1L);
        verify(usuarioRepository).findById(1L);
        verify(comentarioRepository).save(any(Comentario.class));
    }

    @Test
    void testCrearComentario_RecetaNoEncontrada() {
        when(recetaRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            comentarioService.crearComentario(999L, 1L, "Comentario");
        });

        verify(recetaRepository).findById(999L);
        verify(usuarioRepository, never()).findById(any());
    }

    @Test
    void testCrearComentario_UsuarioNoEncontrado() {
        when(recetaRepository.findById(1L)).thenReturn(Optional.of(receta));
        when(usuarioRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            comentarioService.crearComentario(1L, 999L, "Comentario");
        });

        verify(recetaRepository).findById(1L);
        verify(usuarioRepository).findById(999L);
    }

    @Test
    void testObtenerComentariosPorReceta() {
        List<Comentario> comentarios = Arrays.asList(comentario);
        when(comentarioRepository.findByRecetaIdOrderByFechaCreacionDesc(1L)).thenReturn(comentarios);

        List<ComentarioResponse> resultado = comentarioService.obtenerComentariosPorReceta(1L);

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals("Comentario de prueba", resultado.get(0).getComentario());
        assertEquals("Test User", resultado.get(0).getUsuarioNombre());
        verify(comentarioRepository).findByRecetaIdOrderByFechaCreacionDesc(1L);
    }

    @Test
    void testEliminarComentario() {
        when(comentarioRepository.findByIdAndUsuarioId(1L, 1L)).thenReturn(Optional.of(comentario));
        doNothing().when(comentarioRepository).delete(comentario);

        assertDoesNotThrow(() -> {
            comentarioService.eliminarComentario(1L, 1L);
        });

        verify(comentarioRepository).findByIdAndUsuarioId(1L, 1L);
        verify(comentarioRepository).delete(comentario);
    }

    @Test
    void testEliminarComentario_NoEncontrado() {
        when(comentarioRepository.findByIdAndUsuarioId(999L, 1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            comentarioService.eliminarComentario(999L, 1L);
        });

        verify(comentarioRepository).findByIdAndUsuarioId(999L, 1L);
        verify(comentarioRepository, never()).delete(any());
    }

    @Test
    void testContarComentariosPorReceta() {
        when(comentarioRepository.countByRecetaId(1L)).thenReturn(5L);

        long resultado = comentarioService.contarComentariosPorReceta(1L);

        assertEquals(5L, resultado);
        verify(comentarioRepository).countByRecetaId(1L);
    }

    @Test
    void testObtenerComentariosPorReceta_Vacio() {
        when(comentarioRepository.findByRecetaIdOrderByFechaCreacionDesc(1L)).thenReturn(Arrays.asList());

        List<ComentarioResponse> resultado = comentarioService.obtenerComentariosPorReceta(1L);

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
        verify(comentarioRepository).findByRecetaIdOrderByFechaCreacionDesc(1L);
    }
}

