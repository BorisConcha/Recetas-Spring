package com.recetas.recetas.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.recetas.recetas.model.Pedido;
import com.recetas.recetas.model.Usuario;
import com.recetas.recetas.repository.PedidoRepository;
import com.recetas.recetas.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = PagoController.class)
class PagoControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private PedidoRepository pedidoRepository;
    
    @MockBean
    private UsuarioRepository usuarioRepository;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Test
    @WithMockUser(username = "testuser")
    void testSimularPago() throws Exception {
        Usuario usuario = new Usuario();
        usuario.setUsername("testuser");
        
        Pedido pedido = new Pedido();
        pedido.setId(1L);
        pedido.setUsuario(usuario);
        pedido.setEstado("PENDIENTE");
        pedido.setTotal(new BigDecimal("599.99"));
        
        Pedido pedidoConfirmado = new Pedido();
        pedidoConfirmado.setId(1L);
        pedidoConfirmado.setUsuario(usuario);
        pedidoConfirmado.setEstado("CONFIRMADO");
        pedidoConfirmado.setTotal(new BigDecimal("599.99"));
        
        Map<String, Long> request = new HashMap<>();
        request.put("pedidoId", 1L);
        
        when(usuarioRepository.findByUsername("testuser")).thenReturn(Optional.of(usuario));
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));
        when(pedidoRepository.save(any(Pedido.class))).thenReturn(pedidoConfirmado);
        
        mockMvc.perform(post("/api/pagos/simular")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensaje").value("Pago procesado exitosamente"))
                .andExpect(jsonPath("$.estado").value("CONFIRMADO"));
        
        verify(pedidoRepository, times(1)).save(any(Pedido.class));
    }
    
    @Test
    @WithMockUser(username = "testuser")
    void testObtenerEstadoPago() throws Exception {
        Usuario usuario = new Usuario();
        usuario.setUsername("testuser");
        
        Pedido pedido = new Pedido();
        pedido.setId(1L);
        pedido.setUsuario(usuario);
        pedido.setEstado("CONFIRMADO");
        pedido.setTotal(new BigDecimal("599.99"));
        
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));
        
        mockMvc.perform(get("/api/pagos/pedido/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("CONFIRMADO"))
                .andExpect(jsonPath("$.pagado").value(true));
        
        verify(pedidoRepository, times(1)).findById(1L);
    }
}

