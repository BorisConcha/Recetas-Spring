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
}

