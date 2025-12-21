package com.recetas.recetas.service;

import com.recetas.recetas.model.Producto;
import com.recetas.recetas.repository.ProductoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductoServiceTest {
    
    @Mock
    private ProductoRepository productoRepository;
    
    @InjectMocks
    private ProductoService productoService;
    
    private Producto producto;
    
    @BeforeEach
    void setUp() {
        producto = new Producto();
        producto.setId(1L);
        producto.setNombre("Laptop HP");
        producto.setDescripcion("Laptop HP Pavilion");
        producto.setPrecio(new BigDecimal("599.99"));
        producto.setStock(15);
        producto.setCategoria("Electrónica");
        producto.setActivoBoolean(true);
        producto.setFechaCreacion(LocalDateTime.now());
    }
    
    @Test
    void testObtenerTodosLosProductos() {
        List<Producto> productos = Arrays.asList(producto);
        when(productoRepository.findAll()).thenReturn(productos);
        
        List<Producto> resultado = productoService.obtenerTodosLosProductos();
        
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        verify(productoRepository, times(1)).findAll();
    }
    
    @Test
    void testObtenerProductosActivos() {
        List<Producto> productos = Arrays.asList(producto);
        when(productoRepository.findByActivo(1)).thenReturn(productos);
        
        List<Producto> resultado = productoService.obtenerProductosActivos();
        
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        verify(productoRepository, times(1)).findByActivo(1);
    }
    
    @Test
    void testObtenerProductoPorId() {
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));
        
        Optional<Producto> resultado = productoService.obtenerProductoPorId(1L);
        
        assertTrue(resultado.isPresent());
        assertEquals("Laptop HP", resultado.get().getNombre());
        verify(productoRepository, times(1)).findById(1L);
    }
    
    @Test
    void testObtenerProductoPorIdNoEncontrado() {
        when(productoRepository.findById(999L)).thenReturn(Optional.empty());
        
        Optional<Producto> resultado = productoService.obtenerProductoPorId(999L);
        
        assertFalse(resultado.isPresent());
        verify(productoRepository, times(1)).findById(999L);
    }
    
    @Test
    void testGuardarProducto() {
        when(productoRepository.save(any(Producto.class))).thenReturn(producto);
        
        Producto resultado = productoService.guardarProducto(producto);
        
        assertNotNull(resultado);
        assertEquals("Laptop HP", resultado.getNombre());
        verify(productoRepository, times(1)).save(producto);
    }
    
    @Test
    void testEliminarProducto() {
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));
        when(productoRepository.save(any(Producto.class))).thenReturn(producto);
        
        productoService.eliminarProducto(1L);
        
        assertFalse(producto.isActivo());
        verify(productoRepository, times(1)).findById(1L);
        verify(productoRepository, times(1)).save(producto);
    }
    
    @Test
    void testEliminarProductoNoEncontrado() {
        when(productoRepository.findById(999L)).thenReturn(Optional.empty());
        
        productoService.eliminarProducto(999L);
        
        verify(productoRepository, times(1)).findById(999L);
        verify(productoRepository, never()).save(any());
    }
    
    @Test
    void testObtenerProductosRecientes() {
        List<Producto> productos = Arrays.asList(producto);
        when(productoRepository.findProductosActivosRecientes()).thenReturn(productos);
        
        List<Producto> resultado = productoService.obtenerProductosRecientes();
        
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        verify(productoRepository, times(1)).findProductosActivosRecientes();
    }
    
    @Test
    void testObtenerProductoActivoPorId() {
        when(productoRepository.findByIdAndActivo(1L, 1)).thenReturn(Optional.of(producto));
        
        Optional<Producto> resultado = productoService.obtenerProductoActivoPorId(1L);
        
        assertTrue(resultado.isPresent());
        assertEquals("Laptop HP", resultado.get().getNombre());
        verify(productoRepository, times(1)).findByIdAndActivo(1L, 1);
    }
    
    @Test
    void testObtenerProductoActivoPorIdNoEncontrado() {
        when(productoRepository.findByIdAndActivo(999L, 1)).thenReturn(Optional.empty());
        
        Optional<Producto> resultado = productoService.obtenerProductoActivoPorId(999L);
        
        assertFalse(resultado.isPresent());
        verify(productoRepository, times(1)).findByIdAndActivo(999L, 1);
    }
    
    @Test
    void testObtenerProductosPorCategoria() {
        List<Producto> productos = Arrays.asList(producto);
        when(productoRepository.findByCategoriaAndActivo("Electrónica", 1)).thenReturn(productos);
        
        List<Producto> resultado = productoService.obtenerProductosPorCategoria("Electrónica");
        
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        verify(productoRepository, times(1)).findByCategoriaAndActivo("Electrónica", 1);
    }
    
    @Test
    void testActualizarProducto() {
        when(productoRepository.save(any(Producto.class))).thenReturn(producto);
        
        Producto resultado = productoService.actualizarProducto(producto);
        
        assertNotNull(resultado);
        assertEquals("Laptop HP", resultado.getNombre());
        verify(productoRepository, times(1)).save(producto);
    }
}

