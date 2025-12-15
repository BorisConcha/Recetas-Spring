package com.recetas.recetas.controller;

import com.recetas.recetas.model.Receta;
import com.recetas.recetas.service.RecetaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.Model;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BuscarControllerTest {

    @Mock
    private RecetaService recetaService;

    @Mock
    private Model model;

    @InjectMocks
    private BuscarController buscarController;

    private List<Receta> recetas;

    @BeforeEach
    void setUp() {
        Receta receta = new Receta();
        receta.setId(1L);
        receta.setNombre("Receta Test");
        recetas = Arrays.asList(receta);
    }

    @Test
    void testBuscar_PorNombre() {
        when(recetaService.buscarPorNombre("test")).thenReturn(recetas);

        String viewName = buscarController.buscar("test", null, null, null, null, model);

        assertEquals("buscar", viewName);
        verify(recetaService).buscarPorNombre("test");
        verify(model).addAttribute("resultados", recetas);
    }

    @Test
    void testBuscar_PorTipoCocina() {
        when(recetaService.buscarPorTipoCocina("Italiana")).thenReturn(recetas);

        String viewName = buscarController.buscar(null, "Italiana", null, null, null, model);

        assertEquals("buscar", viewName);
        verify(recetaService).buscarPorTipoCocina("Italiana");
    }

    @Test
    void testBuscar_PorIngrediente() {
        when(recetaService.buscarPorIngrediente("tomate")).thenReturn(recetas);

        String viewName = buscarController.buscar(null, null, "tomate", null, null, model);

        assertEquals("buscar", viewName);
        verify(recetaService).buscarPorIngrediente("tomate");
    }

    @Test
    void testBuscar_PorPaisOrigen() {
        when(recetaService.buscarPorPaisOrigen("Italia")).thenReturn(recetas);

        String viewName = buscarController.buscar(null, null, null, "Italia", null, model);

        assertEquals("buscar", viewName);
        verify(recetaService).buscarPorPaisOrigen("Italia");
    }

    @Test
    void testBuscar_PorDificultad() {
        when(recetaService.buscarPorDificultad("Media")).thenReturn(recetas);

        String viewName = buscarController.buscar(null, null, null, null, "Media", model);

        assertEquals("buscar", viewName);
        verify(recetaService).buscarPorDificultad("Media");
    }

    @Test
    void testBuscar_SinParametros() {
        when(recetaService.obtenerTodasLasRecetas()).thenReturn(recetas);

        String viewName = buscarController.buscar(null, null, null, null, null, model);

        assertEquals("buscar", viewName);
        verify(recetaService).obtenerTodasLasRecetas();
    }

    @Test
    void testBuscar_InputInvalido() {
        String viewName = buscarController.buscar("script", null, null, null, null, model);

        assertEquals("buscar", viewName);
        verify(recetaService, never()).buscarPorNombre(any());
    }

    @Test
    void testBuscar_InputSQLInjection() {
        String viewName = buscarController.buscar("SELECT * FROM", null, null, null, null, model);

        assertEquals("buscar", viewName);
        verify(recetaService, never()).buscarPorNombre(any());
    }

    @Test
    void testBuscar_InputVacio() {
        when(recetaService.obtenerTodasLasRecetas()).thenReturn(recetas);
        String viewName = buscarController.buscar("", null, null, null, null, model);
        assertEquals("buscar", viewName);
        verify(recetaService).obtenerTodasLasRecetas();
    }

}

