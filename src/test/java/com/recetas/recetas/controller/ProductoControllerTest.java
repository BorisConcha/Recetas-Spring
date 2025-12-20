package com.recetas.recetas.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.recetas.recetas.dto.ProductoRequest;
import com.recetas.recetas.model.Producto;
import com.recetas.recetas.service.ProductoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ProductoController.class)
class ProductoControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private ProductoService productoService;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Test
    void testObtenerProductosActivos() throws Exception {
        Producto producto = new Producto();
        producto.setId(1L);
        producto.setNombre("Laptop");
        producto.setPrecio(new BigDecimal("599.99"));
        
        List<Producto> productos = Arrays.asList(producto);
        when(productoService.obtenerProductosActivos()).thenReturn(productos);
        
        mockMvc.perform(get("/api/productos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombre").value("Laptop"));
        
        verify(productoService, times(1)).obtenerProductosActivos();
    }
    
    @Test
    void testObtenerProductoPorId() throws Exception {
        Producto producto = new Producto();
        producto.setId(1L);
        producto.setNombre("Laptop");
        
        when(productoService.obtenerProductoActivoPorId(1L)).thenReturn(Optional.of(producto));
        
        mockMvc.perform(get("/api/productos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Laptop"));
        
        verify(productoService, times(1)).obtenerProductoActivoPorId(1L);
    }
    
    @Test
    @WithMockUser(roles = "ADMIN")
    void testCrearProducto() throws Exception {
        ProductoRequest request = new ProductoRequest();
        request.setNombre("Laptop");
        request.setPrecio(new BigDecimal("599.99"));
        request.setStock(10);
        request.setCategoria("Electrónica");
        
        Producto producto = new Producto();
        producto.setId(1L);
        producto.setNombre("Laptop");
        
        when(productoService.guardarProducto(any(Producto.class))).thenReturn(producto);
        
        mockMvc.perform(post("/api/productos")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
        
        verify(productoService, times(1)).guardarProducto(any(Producto.class));
    }
    
    @Test
    @WithMockUser(roles = "ADMIN")
    void testEliminarProducto() throws Exception {
        doNothing().when(productoService).eliminarProducto(1L);
        
        mockMvc.perform(delete("/api/productos/1")
                .with(csrf()))
                .andExpect(status().isOk());
        
        verify(productoService, times(1)).eliminarProducto(1L);
    }
}

