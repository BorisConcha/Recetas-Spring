package com.recetas.recetas.service;

import com.recetas.recetas.model.Receta;
import com.recetas.recetas.model.RecetaCompartida;
import com.recetas.recetas.model.Usuario;
import com.recetas.recetas.repository.RecetaCompartidaRepository;
import com.recetas.recetas.repository.RecetaRepository;
import com.recetas.recetas.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CompartirServiceTest {

    @Mock
    private RecetaCompartidaRepository recetaCompartidaRepository;

    @Mock
    private RecetaRepository recetaRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private CompartirService compartirService;

    private Receta receta;
    private Usuario usuario;
    private RecetaCompartida recetaCompartida;

    @BeforeEach
    void setUp() {
        receta = new Receta();
        receta.setId(1L);
        receta.setNombre("Receta Test");

        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setUsername("testuser");

        recetaCompartida = new RecetaCompartida();
        recetaCompartida.setId(1L);
        recetaCompartida.setReceta(receta);
        recetaCompartida.setUsuario(usuario);
        recetaCompartida.setPlataforma("Facebook");
    }

    @Test
    void testRegistrarCompartido() {
        when(recetaRepository.findById(1L)).thenReturn(Optional.of(receta));
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(recetaCompartidaRepository.save(any(RecetaCompartida.class))).thenReturn(recetaCompartida);

        RecetaCompartida resultado = compartirService.registrarCompartido(1L, 1L, "Facebook");

        assertNotNull(resultado);
        assertEquals("Facebook", resultado.getPlataforma());
        verify(recetaRepository).findById(1L);
        verify(usuarioRepository).findById(1L);
        verify(recetaCompartidaRepository).save(any(RecetaCompartida.class));
    }

    @Test
    void testRegistrarCompartido_RecetaNoEncontrada() {
        when(recetaRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            compartirService.registrarCompartido(999L, 1L, "Facebook");
        });

        verify(recetaRepository).findById(999L);
        verify(usuarioRepository, never()).findById(any());
    }

    @Test
    void testRegistrarCompartido_UsuarioNoEncontrado() {
        when(recetaRepository.findById(1L)).thenReturn(Optional.of(receta));
        when(usuarioRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            compartirService.registrarCompartido(1L, 999L, "Facebook");
        });

        verify(recetaRepository).findById(1L);
        verify(usuarioRepository).findById(999L);
    }

    @Test
    void testContarCompartidosPorReceta() {
        when(recetaCompartidaRepository.countByRecetaId(1L)).thenReturn(5L);

        long resultado = compartirService.contarCompartidosPorReceta(1L);

        assertEquals(5L, resultado);
        verify(recetaCompartidaRepository).countByRecetaId(1L);
    }

    @Test
    void testGenerarLinkCompartir() {
        String baseUrl = "https://example.com";
        String resultado = compartirService.generarLinkCompartir(1L, baseUrl);

        assertEquals("https://example.com/recetas/1", resultado);
    }
}

