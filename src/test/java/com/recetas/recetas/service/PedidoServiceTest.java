package com.recetas.recetas.service;

import com.recetas.recetas.model.*;
import com.recetas.recetas.repository.DetallePedidoRepository;
import com.recetas.recetas.repository.PedidoRepository;
import com.recetas.recetas.repository.ProductoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PedidoServiceTest {
    
    @Mock
    private PedidoRepository pedidoRepository;
    
    @Mock
    private DetallePedidoRepository detallePedidoRepository;
    
    @Mock
    private ProductoRepository productoRepository;
    
    @InjectMocks
    private PedidoService pedidoService;
    
    private Pedido pedido;
    private Usuario usuario;
    private Producto producto;
    private DetallePedido detallePedido;
    
    @BeforeEach
    void setUp() {
        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setUsername("testuser");
        
        producto = new Producto();
        producto.setId(1L);
        producto.setNombre("Laptop");
        producto.setPrecio(new BigDecimal("599.99"));
        producto.setStock(10);
        producto.setActivoBoolean(true);
        
        pedido = new Pedido();
        pedido.setId(1L);
        pedido.setUsuario(usuario);
        pedido.setEstado("PENDIENTE");
        pedido.setTotal(BigDecimal.ZERO);
        pedido.setFechaPedido(LocalDateTime.now());
        
        detallePedido = new DetallePedido();
        detallePedido.setProducto(producto);
        detallePedido.setCantidad(2);
    }
    
    @Test
    void testObtenerPedidosPorUsuario() {
        List<Pedido> pedidos = Arrays.asList(pedido);
        when(pedidoRepository.findByUsuarioOrderByFechaPedidoDesc(usuario)).thenReturn(pedidos);
        
        List<Pedido> resultado = pedidoService.obtenerPedidosPorUsuario(usuario);
        
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        verify(pedidoRepository, times(1)).findByUsuarioOrderByFechaPedidoDesc(usuario);
    }
    
    @Test
    void testObtenerPedidoPorId() {
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));
        
        Optional<Pedido> resultado = pedidoService.obtenerPedidoPorId(1L);
        
        assertTrue(resultado.isPresent());
        assertEquals("PENDIENTE", resultado.get().getEstado());
        verify(pedidoRepository, times(1)).findById(1L);
    }
    
    @Test
    void testCrearPedido() {
        Set<DetallePedido> detalles = new HashSet<>();
        detalles.add(detallePedido);
        
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));
        when(pedidoRepository.save(any(Pedido.class))).thenReturn(pedido);
        when(detallePedidoRepository.save(any(DetallePedido.class))).thenReturn(detallePedido);
        when(productoRepository.save(any(Producto.class))).thenReturn(producto);
        
        Pedido resultado = pedidoService.crearPedido(pedido, detalles);
        
        assertNotNull(resultado);
        verify(pedidoRepository, times(1)).save(pedido);
        verify(detallePedidoRepository, times(1)).save(detallePedido);
        verify(productoRepository, times(1)).save(producto);
    }
    
    @Test
    void testCrearPedidoStockInsuficiente() {
        producto.setStock(1);
        Set<DetallePedido> detalles = new HashSet<>();
        detalles.add(detallePedido);
        
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));
        
        assertThrows(RuntimeException.class, () -> {
            pedidoService.crearPedido(pedido, detalles);
        });
    }
    
    @Test
    void testActualizarEstadoPedido() {
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));
        when(pedidoRepository.save(any(Pedido.class))).thenReturn(pedido);
        
        Pedido resultado = pedidoService.actualizarEstadoPedido(1L, "CONFIRMADO");
        
        assertEquals("CONFIRMADO", resultado.getEstado());
        verify(pedidoRepository, times(1)).findById(1L);
        verify(pedidoRepository, times(1)).save(pedido);
    }
    
    @Test
    void testActualizarEstadoPedidoNoEncontrado() {
        when(pedidoRepository.findById(999L)).thenReturn(Optional.empty());
        
        assertThrows(RuntimeException.class, () -> {
            pedidoService.actualizarEstadoPedido(999L, "CONFIRMADO");
        });
    }
    
    @Test
    void testCancelarPedido() {
        pedido.setEstado("CONFIRMADO");
        List<DetallePedido> detalles = Arrays.asList(detallePedido);
        
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));
        when(detallePedidoRepository.findByPedido(pedido)).thenReturn(detalles);
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));
        when(pedidoRepository.save(any(Pedido.class))).thenReturn(pedido);
        when(productoRepository.save(any(Producto.class))).thenReturn(producto);
        
        pedidoService.cancelarPedido(1L);
        
        assertEquals("CANCELADO", pedido.getEstado());
        verify(pedidoRepository, times(1)).save(pedido);
    }
    
    @Test
    void testCancelarPedidoYaCancelado() {
        pedido.setEstado("CANCELADO");
        
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));
        
        pedidoService.cancelarPedido(1L);
        
        // No debe restaurar stock ni cambiar estado
        verify(detallePedidoRepository, never()).findByPedido(any());
        verify(pedidoRepository, never()).save(any());
    }
    
    @Test
    void testCancelarPedidoYaEntregado() {
        pedido.setEstado("ENTREGADO");
        
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));
        
        pedidoService.cancelarPedido(1L);
        
        // No debe restaurar stock ni cambiar estado
        verify(detallePedidoRepository, never()).findByPedido(any());
        verify(pedidoRepository, never()).save(any());
    }
    
    @Test
    void testCancelarPedidoNoEncontrado() {
        when(pedidoRepository.findById(999L)).thenReturn(Optional.empty());
        
        pedidoService.cancelarPedido(999L);
        
        verify(pedidoRepository, never()).save(any());
    }
    
    @Test
    void testObtenerTodosLosPedidos() {
        List<Pedido> pedidos = Arrays.asList(pedido);
        when(pedidoRepository.findAll()).thenReturn(pedidos);
        
        List<Pedido> resultado = pedidoService.obtenerTodosLosPedidos();
        
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        verify(pedidoRepository, times(1)).findAll();
    }
    
    @Test
    void testObtenerPedidosPorEstado() {
        List<Pedido> pedidos = Arrays.asList(pedido);
        when(pedidoRepository.findByEstado("PENDIENTE")).thenReturn(pedidos);
        
        List<Pedido> resultado = pedidoService.obtenerPedidosPorEstado("PENDIENTE");
        
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        verify(pedidoRepository, times(1)).findByEstado("PENDIENTE");
    }
    
    @Test
    void testCrearPedidoProductoNoDisponible() {
        producto.setActivoBoolean(false);
        Set<DetallePedido> detalles = new HashSet<>();
        detalles.add(detallePedido);
        
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));
        
        assertThrows(RuntimeException.class, () -> {
            pedidoService.crearPedido(pedido, detalles);
        });
    }
    
    @Test
    void testCrearPedidoProductoNoEncontrado() {
        Set<DetallePedido> detalles = new HashSet<>();
        detalles.add(detallePedido);
        
        when(productoRepository.findById(1L)).thenReturn(Optional.empty());
        
        Pedido resultado = pedidoService.crearPedido(pedido, detalles);
        
        // Debe crear el pedido pero sin detalles
        verify(pedidoRepository, times(1)).save(pedido);
    }
    
    @Test
    void testCrearPedidoConMultiplesDetalles() {
        Producto producto2 = new Producto();
        producto2.setId(2L);
        producto2.setPrecio(new BigDecimal("299.99"));
        producto2.setStock(5);
        producto2.setActivoBoolean(true);
        
        DetallePedido detalle2 = new DetallePedido();
        detalle2.setProducto(producto2);
        detalle2.setCantidad(1);
        
        Set<DetallePedido> detalles = new HashSet<>();
        detalles.add(detallePedido);
        detalles.add(detalle2);
        
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));
        when(productoRepository.findById(2L)).thenReturn(Optional.of(producto2));
        when(pedidoRepository.save(any(Pedido.class))).thenReturn(pedido);
        when(detallePedidoRepository.save(any(DetallePedido.class))).thenReturn(detallePedido);
        when(productoRepository.save(any(Producto.class))).thenReturn(producto);
        
        Pedido resultado = pedidoService.crearPedido(pedido, detalles);
        
        assertNotNull(resultado);
        verify(detallePedidoRepository, times(2)).save(any(DetallePedido.class));
        verify(productoRepository, times(2)).save(any(Producto.class));
    }
}

