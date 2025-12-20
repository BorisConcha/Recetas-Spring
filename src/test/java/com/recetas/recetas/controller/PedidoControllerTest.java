package com.recetas.recetas.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.recetas.recetas.model.Pedido;
import com.recetas.recetas.model.Usuario;
import com.recetas.recetas.repository.UsuarioRepository;
import com.recetas.recetas.service.PedidoService;
import com.recetas.recetas.service.ProductoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = PedidoController.class)
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
}

