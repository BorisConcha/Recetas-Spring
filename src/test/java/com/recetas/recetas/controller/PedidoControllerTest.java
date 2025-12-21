package com.recetas.recetas.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.recetas.recetas.dto.PedidoRequest;
import com.recetas.recetas.model.Pedido;
import com.recetas.recetas.model.Usuario;
import com.recetas.recetas.repository.UsuarioRepository;
import com.recetas.recetas.service.PedidoService;
import com.recetas.recetas.service.ProductoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.recetas.recetas.model.Producto;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import org.springframework.test.context.TestPropertySource;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import com.recetas.recetas.config.SecurityConfig;
import com.recetas.recetas.config.JwtAuthenticationFilter;

@WebMvcTest(controllers = PedidoController.class,
    excludeAutoConfiguration = {org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class},
    excludeFilters = {
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {SecurityConfig.class, JwtAuthenticationFilter.class})
    })
@TestPropertySource(properties = {
    "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration"
})
class PedidoControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private PedidoService pedidoService;
    
    @MockBean
    private ProductoService productoService;
    
    @MockBean
    private UsuarioRepository usuarioRepository;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Test
    @WithMockUser(username = "testuser")
    void testObtenerMisPedidos() throws Exception {
        Pedido pedido = new Pedido();
        pedido.setId(1L);
        pedido.setEstado("PENDIENTE");
        
        Usuario usuario = new Usuario();
        usuario.setUsername("testuser");
        
        List<Pedido> pedidos = Arrays.asList(pedido);
        when(usuarioRepository.findByUsername("testuser")).thenReturn(Optional.of(usuario));
        when(pedidoService.obtenerPedidosPorUsuario(any(Usuario.class))).thenReturn(pedidos);
        
        mockMvc.perform(get("/api/pedidos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].estado").value("PENDIENTE"));
        
        verify(pedidoService, times(1)).obtenerPedidosPorUsuario(any(Usuario.class));
    }
    
    @Test
    @WithMockUser(username = "testuser")
    void testObtenerPedidoPorId() throws Exception {
        Pedido pedido = new Pedido();
        pedido.setId(1L);
        pedido.setEstado("PENDIENTE");
        
        Usuario usuario = new Usuario();
        usuario.setUsername("testuser");
        pedido.setUsuario(usuario);
        
        when(usuarioRepository.findByUsername("testuser")).thenReturn(Optional.of(usuario));
        when(pedidoService.obtenerPedidoPorId(1L)).thenReturn(Optional.of(pedido));
        
        mockMvc.perform(get("/api/pedidos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("PENDIENTE"));
        
        verify(pedidoService, times(1)).obtenerPedidoPorId(1L);
    }
    
    @Test
    @WithMockUser(username = "testuser")
    void testObtenerPedidoPorId_NoPerteneceAlUsuario() throws Exception {
        Pedido pedido = new Pedido();
        pedido.setId(1L);
        pedido.setEstado("PENDIENTE");
        
        Usuario otroUsuario = new Usuario();
        otroUsuario.setUsername("otheruser");
        pedido.setUsuario(otroUsuario);
        
        Usuario usuario = new Usuario();
        usuario.setUsername("testuser");
        
        when(usuarioRepository.findByUsername("testuser")).thenReturn(Optional.of(usuario));
        when(pedidoService.obtenerPedidoPorId(1L)).thenReturn(Optional.of(pedido));
        
        mockMvc.perform(get("/api/pedidos/1"))
                .andExpect(status().isForbidden());
        
        verify(pedidoService, times(1)).obtenerPedidoPorId(1L);
    }
    
    @Test
    @WithMockUser(username = "testuser")
    void testObtenerPedidoPorId_NoEncontrado() throws Exception {
        Usuario usuario = new Usuario();
        usuario.setUsername("testuser");
        
        when(usuarioRepository.findByUsername("testuser")).thenReturn(Optional.of(usuario));
        when(pedidoService.obtenerPedidoPorId(999L)).thenReturn(Optional.empty());
        
        mockMvc.perform(get("/api/pedidos/999"))
                .andExpect(status().isNotFound());
        
        verify(pedidoService, times(1)).obtenerPedidoPorId(999L);
    }
    
    @Test
    @WithMockUser(username = "testuser")
    void testCrearPedido() throws Exception {
        PedidoRequest request = new PedidoRequest();
        request.setDireccionEntrega("Calle 123");
        request.setTelefonoContacto("123456789");
        
        PedidoRequest.DetallePedidoRequest detalleReq = new PedidoRequest.DetallePedidoRequest();
        detalleReq.setProductoId(1L);
        detalleReq.setCantidad(2);
        request.setDetalles(Arrays.asList(detalleReq));
        
        Usuario usuario = new Usuario();
        usuario.setUsername("testuser");
        
        Producto producto = new Producto();
        producto.setId(1L);
        producto.setStock(10);
        producto.setNombre("Laptop");
        
        Pedido pedido = new Pedido();
        pedido.setId(1L);
        
        when(usuarioRepository.findByUsername("testuser")).thenReturn(Optional.of(usuario));
        when(productoService.obtenerProductoActivoPorId(1L)).thenReturn(Optional.of(producto));
        when(pedidoService.crearPedido(any(Pedido.class), any())).thenReturn(pedido);
        
        mockMvc.perform(post("/api/pedidos")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
        
        verify(pedidoService, times(1)).crearPedido(any(Pedido.class), any());
    }
    
    @Test
    @WithMockUser(username = "testuser")
    void testCrearPedido_ProductoNoEncontrado() throws Exception {
        PedidoRequest request = new PedidoRequest();
        request.setDireccionEntrega("Calle 123");
        request.setTelefonoContacto("123456789");
        
        PedidoRequest.DetallePedidoRequest detalleReq = new PedidoRequest.DetallePedidoRequest();
        detalleReq.setProductoId(999L);
        detalleReq.setCantidad(2);
        request.setDetalles(Arrays.asList(detalleReq));
        
        Usuario usuario = new Usuario();
        usuario.setUsername("testuser");
        
        when(usuarioRepository.findByUsername("testuser")).thenReturn(Optional.of(usuario));
        when(productoService.obtenerProductoActivoPorId(999L)).thenReturn(Optional.empty());
        
        mockMvc.perform(post("/api/pedidos")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
        
        verify(pedidoService, never()).crearPedido(any(), any());
    }
    
    @Test
    @WithMockUser(username = "testuser")
    void testCancelarPedido() throws Exception {
        Usuario usuario = new Usuario();
        usuario.setUsername("testuser");
        
        Pedido pedido = new Pedido();
        pedido.setId(1L);
        pedido.setUsuario(usuario);
        pedido.setEstado("PENDIENTE");
        
        when(pedidoService.obtenerPedidoPorId(1L)).thenReturn(Optional.of(pedido));
        doNothing().when(pedidoService).cancelarPedido(1L);
        
        mockMvc.perform(put("/api/pedidos/1/cancelar")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensaje").value("Pedido cancelado correctamente"));
        
        verify(pedidoService, times(1)).cancelarPedido(1L);
    }
    
    @Test
    @WithMockUser(username = "testuser")
    void testCancelarPedido_NoEncontrado() throws Exception {
        when(pedidoService.obtenerPedidoPorId(999L)).thenReturn(Optional.empty());
        
        mockMvc.perform(put("/api/pedidos/999/cancelar")
                .with(csrf()))
                .andExpect(status().isNotFound());
        
        verify(pedidoService, never()).cancelarPedido(any());
    }
    
    @Test
    @WithMockUser(username = "otheruser")
    void testCancelarPedido_SinPermiso() throws Exception {
        Usuario otroUsuario = new Usuario();
        otroUsuario.setUsername("otheruser");
        
        Usuario usuario = new Usuario();
        usuario.setUsername("testuser");
        
        Pedido pedido = new Pedido();
        pedido.setId(1L);
        pedido.setUsuario(usuario);
        pedido.setEstado("PENDIENTE");
        
        when(pedidoService.obtenerPedidoPorId(1L)).thenReturn(Optional.of(pedido));
        
        mockMvc.perform(put("/api/pedidos/1/cancelar")
                .with(csrf()))
                .andExpect(status().isForbidden());
        
        verify(pedidoService, never()).cancelarPedido(any());
    }
    
    @Test
    @WithMockUser(roles = "ADMIN")
    void testActualizarEstadoPedido() throws Exception {
        Pedido pedido = new Pedido();
        pedido.setId(1L);
        pedido.setEstado("CONFIRMADO");
        
        Map<String, String> request = new HashMap<>();
        request.put("estado", "ENVIADO");
        
        when(pedidoService.actualizarEstadoPedido(1L, "ENVIADO")).thenReturn(pedido);
        
        mockMvc.perform(put("/api/pedidos/1/estado")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
        
        verify(pedidoService, times(1)).actualizarEstadoPedido(1L, "ENVIADO");
    }
    
    @Test
    @WithMockUser(roles = "ADMIN")
    void testObtenerTodosLosPedidos() throws Exception {
        Pedido pedido = new Pedido();
        pedido.setId(1L);
        
        List<Pedido> pedidos = Arrays.asList(pedido);
        when(pedidoService.obtenerTodosLosPedidos()).thenReturn(pedidos);
        
        mockMvc.perform(get("/api/pedidos/admin/todos"))
                .andExpect(status().isOk());
        
        verify(pedidoService, times(1)).obtenerTodosLosPedidos();
    }
    
    @Test
    @WithMockUser(roles = "ADMIN")
    void testObtenerPedidosPorEstado() throws Exception {
        Pedido pedido = new Pedido();
        pedido.setId(1L);
        pedido.setEstado("PENDIENTE");
        
        List<Pedido> pedidos = Arrays.asList(pedido);
        when(pedidoService.obtenerPedidosPorEstado("PENDIENTE")).thenReturn(pedidos);
        
        mockMvc.perform(get("/api/pedidos/admin/estado/PENDIENTE"))
                .andExpect(status().isOk());
        
        verify(pedidoService, times(1)).obtenerPedidosPorEstado("PENDIENTE");
    }
    
    @Test
    @WithMockUser(username = "testuser")
    void testObtenerMisPedidos_UsuarioNoEncontrado() throws Exception {
        when(usuarioRepository.findByUsername("testuser")).thenReturn(Optional.empty());
        
        mockMvc.perform(get("/api/pedidos"))
                .andExpect(status().isNotFound());
    }
    
    @Test
    @WithMockUser(username = "testuser")
    void testCrearPedido_StockInsuficiente() throws Exception {
        PedidoRequest request = new PedidoRequest();
        request.setDireccionEntrega("Calle 123");
        request.setTelefonoContacto("123456789");
        
        PedidoRequest.DetallePedidoRequest detalleReq = new PedidoRequest.DetallePedidoRequest();
        detalleReq.setProductoId(1L);
        detalleReq.setCantidad(100);
        request.setDetalles(Arrays.asList(detalleReq));
        
        Usuario usuario = new Usuario();
        usuario.setUsername("testuser");
        
        Producto producto = new Producto();
        producto.setId(1L);
        producto.setStock(10);
        producto.setNombre("Laptop");
        
        when(usuarioRepository.findByUsername("testuser")).thenReturn(Optional.of(usuario));
        when(productoService.obtenerProductoActivoPorId(1L)).thenReturn(Optional.of(producto));
        
        mockMvc.perform(post("/api/pedidos")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
        
        verify(pedidoService, never()).crearPedido(any(), any());
    }
    
    @Test
    @WithMockUser(username = "testuser")
    void testCrearPedido_Excepcion() throws Exception {
        PedidoRequest request = new PedidoRequest();
        request.setDireccionEntrega("Calle 123");
        request.setTelefonoContacto("123456789");
        
        PedidoRequest.DetallePedidoRequest detalleReq = new PedidoRequest.DetallePedidoRequest();
        detalleReq.setProductoId(1L);
        detalleReq.setCantidad(2);
        request.setDetalles(Arrays.asList(detalleReq));
        
        Usuario usuario = new Usuario();
        usuario.setUsername("testuser");
        
        Producto producto = new Producto();
        producto.setId(1L);
        producto.setStock(10);
        
        when(usuarioRepository.findByUsername("testuser")).thenReturn(Optional.of(usuario));
        when(productoService.obtenerProductoActivoPorId(1L)).thenReturn(Optional.of(producto));
        when(pedidoService.crearPedido(any(Pedido.class), any())).thenThrow(new RuntimeException("Error de base de datos"));
        
        mockMvc.perform(post("/api/pedidos")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
    
    @Test
    @WithMockUser(roles = "ADMIN")
    void testActualizarEstadoPedido_SinEstado() throws Exception {
        Map<String, String> request = new HashMap<>();
        
        mockMvc.perform(put("/api/pedidos/1/estado")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
        
        verify(pedidoService, never()).actualizarEstadoPedido(any(), any());
    }
    
    @Test
    @WithMockUser(roles = "ADMIN")
    void testActualizarEstadoPedido_EstadoVacio() throws Exception {
        Map<String, String> request = new HashMap<>();
        request.put("estado", "");
        
        mockMvc.perform(put("/api/pedidos/1/estado")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
        
        verify(pedidoService, never()).actualizarEstadoPedido(any(), any());
    }
    
    @Test
    @WithMockUser(roles = "ADMIN")
    void testActualizarEstadoPedido_Excepcion() throws Exception {
        Map<String, String> request = new HashMap<>();
        request.put("estado", "ENVIADO");
        
        when(pedidoService.actualizarEstadoPedido(1L, "ENVIADO")).thenThrow(new RuntimeException("Error de base de datos"));
        
        mockMvc.perform(put("/api/pedidos/1/estado")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
    
    @Test
    @WithMockUser(username = "testuser", roles = "ADMIN")
    void testCancelarPedido_ComoAdmin() throws Exception {
        Usuario otroUsuario = new Usuario();
        otroUsuario.setUsername("otheruser");
        
        Pedido pedido = new Pedido();
        pedido.setId(1L);
        pedido.setUsuario(otroUsuario);
        pedido.setEstado("PENDIENTE");
        
        when(pedidoService.obtenerPedidoPorId(1L)).thenReturn(Optional.of(pedido));
        doNothing().when(pedidoService).cancelarPedido(1L);
        
        mockMvc.perform(put("/api/pedidos/1/cancelar")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensaje").value("Pedido cancelado correctamente"));
        
        verify(pedidoService, times(1)).cancelarPedido(1L);
    }
    
    @Test
    @WithMockUser(username = "testuser", roles = "ADMIN")
    void testCancelarPedido_AdminPuedeCancelar() throws Exception {
        Usuario otroUsuario = new Usuario();
        otroUsuario.setUsername("otheruser");
        
        Pedido pedido = new Pedido();
        pedido.setId(1L);
        pedido.setUsuario(otroUsuario);
        pedido.setEstado("PENDIENTE");
        
        when(pedidoService.obtenerPedidoPorId(1L)).thenReturn(Optional.of(pedido));
        doNothing().when(pedidoService).cancelarPedido(1L);
        
        mockMvc.perform(put("/api/pedidos/1/cancelar")
                .with(csrf()))
                .andExpect(status().isOk());
        
        verify(pedidoService, times(1)).cancelarPedido(1L);
    }
    
    @Test
    @WithMockUser(username = "testuser")
    void testCancelarPedido_Excepcion() throws Exception {
        Usuario usuario = new Usuario();
        usuario.setUsername("testuser");
        
        Pedido pedido = new Pedido();
        pedido.setId(1L);
        pedido.setUsuario(usuario);
        pedido.setEstado("PENDIENTE");
        
        when(pedidoService.obtenerPedidoPorId(1L)).thenReturn(Optional.of(pedido));
        doThrow(new RuntimeException("Error de base de datos")).when(pedidoService).cancelarPedido(1L);
        
        mockMvc.perform(put("/api/pedidos/1/cancelar")
                .with(csrf()))
                .andExpect(status().isBadRequest());
    }
}

