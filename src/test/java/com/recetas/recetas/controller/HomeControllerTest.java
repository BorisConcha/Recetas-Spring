package com.recetas.recetas.controller;

import com.recetas.recetas.model.Anuncio;
import com.recetas.recetas.model.Receta;
import com.recetas.recetas.service.AnuncioService;
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
class HomeControllerTest {

    @Mock
    private RecetaService recetaService;

    @Mock
    private AnuncioService anuncioService;

    @Mock
    private Model model;

    @InjectMocks
    private HomeController homeController;

    private List<Receta> recetasRecientes;
    private List<Receta> recetasPopulares;
    private List<Anuncio> anuncios;

    @BeforeEach
    void setUp() {
        Receta receta = new Receta();
        receta.setId(1L);
        receta.setNombre("Receta Test");

        recetasRecientes = Arrays.asList(receta);
        recetasPopulares = Arrays.asList(receta);

        Anuncio anuncio = new Anuncio();
        anuncio.setId(1L);
        anuncios = Arrays.asList(anuncio);
    }

    @Test
    void testInicio() {
        when(recetaService.obtenerRecetasRecientes()).thenReturn(recetasRecientes);
        when(recetaService.obtenerRecetasPopulares()).thenReturn(recetasPopulares);
        when(anuncioService.obtenerAnunciosActivos()).thenReturn(anuncios);

        String viewName = homeController.inicio(model);

        assertEquals("inicio", viewName);
        verify(recetaService).obtenerRecetasRecientes();
        verify(recetaService).obtenerRecetasPopulares();
        verify(anuncioService).obtenerAnunciosActivos();
        verify(model).addAttribute("recetasRecientes", recetasRecientes);
        verify(model).addAttribute("recetasPopulares", recetasPopulares);
        verify(model).addAttribute("anuncios", anuncios);
    }

    @Test
    void testLogin() {
        String viewName = homeController.login();

        assertEquals("login", viewName);
    }
}

