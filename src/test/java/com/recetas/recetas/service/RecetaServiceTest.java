package com.recetas.recetas.service;

import com.recetas.recetas.model.Receta;
import com.recetas.recetas.repository.RecetaRepository;
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
class RecetaServiceTest {

    @Mock
    private RecetaRepository recetaRepository;

    @InjectMocks
    private RecetaService recetaService;

    private Receta receta;

    @BeforeEach
    void setUp() {
        receta = new Receta();
        receta.setId(1L);
        receta.setNombre("Receta Test");
        receta.setTipoCocina("Italiana");
        receta.setPaisOrigen("Italia");
        receta.setDificultad("Media");
        receta.setTiempoCoccion(30);
        receta.setDescripcion("Descripción test");
        receta.setIngredientes("Ingredientes test");
        receta.setInstrucciones("Instrucciones test");
        receta.setFechaCreacion(LocalDateTime.now());
        receta.setPopularidad(10);
    }

    @Test
    void testObtenerRecetasRecientes() {
        List<Receta> recetas = Arrays.asList(receta);
        when(recetaRepository.findTop6ByOrderByFechaCreacionDesc()).thenReturn(recetas);

        List<Receta> resultado = recetaService.obtenerRecetasRecientes();

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals("Receta Test", resultado.get(0).getNombre());
        verify(recetaRepository).findTop6ByOrderByFechaCreacionDesc();
    }

    @Test
    void testObtenerRecetasPopulares() {
        List<Receta> recetas = Arrays.asList(receta);
        when(recetaRepository.findTop6ByOrderByPopularidadDesc()).thenReturn(recetas);

        List<Receta> resultado = recetaService.obtenerRecetasPopulares();

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        verify(recetaRepository).findTop6ByOrderByPopularidadDesc();
    }

    @Test
    void testBuscarPorNombre() {
        List<Receta> recetas = Arrays.asList(receta);
        when(recetaRepository.findByNombreContainingIgnoreCase("test")).thenReturn(recetas);

        List<Receta> resultado = recetaService.buscarPorNombre("test");

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        verify(recetaRepository).findByNombreContainingIgnoreCase("test");
    }

    @Test
    void testBuscarPorTipoCocina() {
        List<Receta> recetas = Arrays.asList(receta);
        when(recetaRepository.findByTipoCocinaContainingIgnoreCase("Italiana")).thenReturn(recetas);

        List<Receta> resultado = recetaService.buscarPorTipoCocina("Italiana");

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        verify(recetaRepository).findByTipoCocinaContainingIgnoreCase("Italiana");
    }

    @Test
    void testBuscarPorPaisOrigen() {
        List<Receta> recetas = Arrays.asList(receta);
        when(recetaRepository.findByPaisOrigenContainingIgnoreCase("Italia")).thenReturn(recetas);

        List<Receta> resultado = recetaService.buscarPorPaisOrigen("Italia");

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        verify(recetaRepository).findByPaisOrigenContainingIgnoreCase("Italia");
    }

    @Test
    void testBuscarPorDificultad() {
        List<Receta> recetas = Arrays.asList(receta);
        when(recetaRepository.findByDificultadContainingIgnoreCase("Media")).thenReturn(recetas);

        List<Receta> resultado = recetaService.buscarPorDificultad("Media");

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        verify(recetaRepository).findByDificultadContainingIgnoreCase("Media");
    }

    @Test
    void testBuscarPorIngrediente() {
        List<Receta> recetas = Arrays.asList(receta);
        when(recetaRepository.findByIngredientesContainingIgnoreCase("tomate")).thenReturn(recetas);

        List<Receta> resultado = recetaService.buscarPorIngrediente("tomate");

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        verify(recetaRepository).findByIngredientesContainingIgnoreCase("tomate");
    }

    @Test
    void testObtenerRecetaPorId_Existe() {
        when(recetaRepository.findById(1L)).thenReturn(Optional.of(receta));

        Optional<Receta> resultado = recetaService.obtenerRecetaPorId(1L);

        assertTrue(resultado.isPresent());
        assertEquals("Receta Test", resultado.get().getNombre());
        verify(recetaRepository).findById(1L);
    }

    @Test
    void testObtenerRecetaPorId_NoExiste() {
        when(recetaRepository.findById(999L)).thenReturn(Optional.empty());

        Optional<Receta> resultado = recetaService.obtenerRecetaPorId(999L);

        assertFalse(resultado.isPresent());
        verify(recetaRepository).findById(999L);
    }

    @Test
    void testObtenerTodasLasRecetas() {
        List<Receta> recetas = Arrays.asList(receta);
        when(recetaRepository.findAll()).thenReturn(recetas);

        List<Receta> resultado = recetaService.obtenerTodasLasRecetas();

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        verify(recetaRepository).findAll();
    }

    @Test
    void testGuardarReceta() {
        when(recetaRepository.save(any(Receta.class))).thenReturn(receta);

        Receta resultado = recetaService.guardarReceta(receta);

        assertNotNull(resultado);
        assertEquals("Receta Test", resultado.getNombre());
        verify(recetaRepository).save(receta);
    }

    @Test
    void testObtenerRecetasRecientes_Vacio() {
        when(recetaRepository.findTop6ByOrderByFechaCreacionDesc()).thenReturn(Arrays.asList());

        List<Receta> resultado = recetaService.obtenerRecetasRecientes();

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
        verify(recetaRepository).findTop6ByOrderByFechaCreacionDesc();
    }

    @Test
    void testObtenerRecetasPopulares_Vacio() {
        when(recetaRepository.findTop6ByOrderByPopularidadDesc()).thenReturn(Arrays.asList());

        List<Receta> resultado = recetaService.obtenerRecetasPopulares();

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
        verify(recetaRepository).findTop6ByOrderByPopularidadDesc();
    }

    @Test
    void testBuscarPorNombre_Vacio() {
        when(recetaRepository.findByNombreContainingIgnoreCase("test")).thenReturn(Arrays.asList());

        List<Receta> resultado = recetaService.buscarPorNombre("test");

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
    }
}

