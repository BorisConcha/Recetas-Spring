package com.recetas.recetas.controller;

import com.recetas.recetas.model.Anuncio;
import com.recetas.recetas.model.Producto;
import com.recetas.recetas.service.AnuncioService;
import com.recetas.recetas.service.ProductoService;
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
    private ProductoService productoService;

    @Mock
    private AnuncioService anuncioService;

    @Mock
    private Model model;

    @InjectMocks
    private HomeController homeController;

    private List<Producto> productosRecientes;
    private List<Producto> productos;
    private List<Anuncio> anuncios;

    @BeforeEach
    void setUp() {
        Producto producto = new Producto();
        producto.setId(1L);
        producto.setNombre("Producto Test");

        productosRecientes = Arrays.asList(producto);
        productos = Arrays.asList(producto);

        Anuncio anuncio = new Anuncio();
        anuncio.setId(1L);
        anuncios = Arrays.asList(anuncio);
    }

    @Test
    void testInicio() {
        when(productoService.obtenerProductosRecientes()).thenReturn(productosRecientes);
        when(productoService.obtenerProductosActivos()).thenReturn(productos);
        when(anuncioService.obtenerAnunciosActivos()).thenReturn(anuncios);

        String viewName = homeController.inicio(model);

        assertEquals("inicio", viewName);
        verify(productoService).obtenerProductosRecientes();
        verify(productoService).obtenerProductosActivos();
        verify(anuncioService).obtenerAnunciosActivos();
        verify(model).addAttribute("productosRecientes", productosRecientes);
        verify(model).addAttribute("productos", productos);
        verify(model).addAttribute("anuncios", anuncios);
    }

    @Test
    void testLogin() {
        String viewName = homeController.login();
        assertEquals("login", viewName);
    }
    
    @Test
    void testRegistro() {
        String viewName = homeController.registro();
        assertEquals("registro", viewName);
    }
    
    @Test
    void testRecuperarPassword() {
        String viewName = homeController.recuperarPassword();
        assertEquals("recuperar-password", viewName);
    }
    
    @Test
    void testPerfil() {
        String viewName = homeController.perfil();
        assertEquals("perfil", viewName);
    }
}

