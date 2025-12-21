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
import org.springframework.test.context.TestPropertySource;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import com.recetas.recetas.config.SecurityConfig;
import com.recetas.recetas.config.JwtAuthenticationFilter;

@WebMvcTest(controllers = PagoController.class,
    excludeAutoConfiguration = {org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class},
    excludeFilters = {
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {SecurityConfig.class, JwtAuthenticationFilter.class})
    })
@TestPropertySource(properties = {
    "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration"
})
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
        
        Map<String, Long> request = new HashMap<>();
        request.put("pedidoId", 1L);
        
        when(usuarioRepository.findByUsername("testuser")).thenReturn(Optional.of(usuario));
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));
        when(pedidoRepository.save(any(Pedido.class))).thenReturn(pedido);
        
        mockMvc.perform(post("/api/pagos/simular")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensaje").value("Pago procesado exitosamente"))
                .andExpect(jsonPath("$.pedidoId").value(1))
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
                .andExpect(jsonPath("$.pedidoId").value(1))
                .andExpect(jsonPath("$.estado").value("CONFIRMADO"))
                .andExpect(jsonPath("$.monto").value(599.99))
                .andExpect(jsonPath("$.pagado").value(true));
        
        verify(pedidoRepository, times(1)).findById(1L);
    }
    
    @Test
    @WithMockUser(username = "testuser")
    void testSimularPago_UsuarioNoEncontrado() throws Exception {
        Map<String, Long> request = new HashMap<>();
        request.put("pedidoId", 1L);
        
        when(usuarioRepository.findByUsername("testuser")).thenReturn(Optional.empty());
        
        mockMvc.perform(post("/api/pagos/simular")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
        
        verify(pedidoRepository, never()).findById(any());
    }
    
    @Test
    @WithMockUser(username = "testuser")
    void testSimularPago_PedidoNoPerteneceAlUsuario() throws Exception {
        Usuario otroUsuario = new Usuario();
        otroUsuario.setUsername("otheruser");
        
        Usuario usuario = new Usuario();
        usuario.setUsername("testuser");
        
        Pedido pedido = new Pedido();
        pedido.setId(1L);
        pedido.setUsuario(otroUsuario);
        pedido.setEstado("PENDIENTE");
        
        Map<String, Long> request = new HashMap<>();
        request.put("pedidoId", 1L);
        
        when(usuarioRepository.findByUsername("testuser")).thenReturn(Optional.of(usuario));
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));
        
        mockMvc.perform(post("/api/pagos/simular")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
        
        verify(pedidoRepository, never()).save(any());
    }
    
    @Test
    @WithMockUser(username = "testuser")
    void testSimularPago_PedidoNoPendiente() throws Exception {
        Usuario usuario = new Usuario();
        usuario.setUsername("testuser");
        
        Pedido pedido = new Pedido();
        pedido.setId(1L);
        pedido.setUsuario(usuario);
        pedido.setEstado("CONFIRMADO");
        
        Map<String, Long> request = new HashMap<>();
        request.put("pedidoId", 1L);
        
        when(usuarioRepository.findByUsername("testuser")).thenReturn(Optional.of(usuario));
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));
        
        mockMvc.perform(post("/api/pagos/simular")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
        
        verify(pedidoRepository, never()).save(any());
    }
    
    @Test
    @WithMockUser(username = "testuser")
    void testSimularPago_PedidoNoEncontrado() throws Exception {
        Usuario usuario = new Usuario();
        usuario.setUsername("testuser");
        
        Map<String, Long> request = new HashMap<>();
        request.put("pedidoId", 999L);
        
        when(usuarioRepository.findByUsername("testuser")).thenReturn(Optional.of(usuario));
        when(pedidoRepository.findById(999L)).thenReturn(Optional.empty());
        
        mockMvc.perform(post("/api/pagos/simular")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
        
        verify(pedidoRepository, never()).save(any());
    }
    
    @Test
    @WithMockUser(username = "testuser")
    void testSimularPago_SinPedidoId() throws Exception {
        Usuario usuario = new Usuario();
        usuario.setUsername("testuser");
        
        Map<String, Long> request = new HashMap<>();
        // No agregamos pedidoId, debería ser null
        
        when(usuarioRepository.findByUsername("testuser")).thenReturn(Optional.of(usuario));
        
        mockMvc.perform(post("/api/pagos/simular")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("El ID del pedido es obligatorio"));
    }
    
    @Test
    @WithMockUser(username = "testuser")
    void testObtenerEstadoPago_NoPerteneceAlUsuario() throws Exception {
        Usuario otroUsuario = new Usuario();
        otroUsuario.setUsername("otheruser");
        
        Pedido pedido = new Pedido();
        pedido.setId(1L);
        pedido.setUsuario(otroUsuario);
        
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));
        
        mockMvc.perform(get("/api/pagos/pedido/1"))
                .andExpect(status().isForbidden());
    }
    
    @Test
    @WithMockUser(username = "testuser")
    void testObtenerEstadoPago_NoEncontrado() throws Exception {
        when(pedidoRepository.findById(999L)).thenReturn(Optional.empty());
        
        mockMvc.perform(get("/api/pagos/pedido/999"))
                .andExpect(status().isNotFound());
    }
    
    @Test
    @WithMockUser(username = "testuser")
    void testObtenerEstadoPago_Pendiente() throws Exception {
        Usuario usuario = new Usuario();
        usuario.setUsername("testuser");
        
        Pedido pedido = new Pedido();
        pedido.setId(1L);
        pedido.setUsuario(usuario);
        pedido.setEstado("PENDIENTE");
        pedido.setTotal(new BigDecimal("599.99"));
        
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));
        
        mockMvc.perform(get("/api/pagos/pedido/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("PENDIENTE"))
                .andExpect(jsonPath("$.pagado").value(false));
        
        verify(pedidoRepository, times(1)).findById(1L);
    }
    
    @Test
    @WithMockUser(username = "testuser")
    void testSimularPago_Excepcion() throws Exception {
        Map<String, Long> request = new HashMap<>();
        request.put("pedidoId", 1L);
        
        when(usuarioRepository.findByUsername("testuser")).thenReturn(Optional.of(new Usuario()));
        when(pedidoRepository.findById(1L)).thenThrow(new RuntimeException("Error de base de datos"));
        
        mockMvc.perform(post("/api/pagos/simular")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
    
    @Test
    @WithMockUser(username = "testuser")
    void testObtenerEstadoPago_Excepcion() throws Exception {
        when(pedidoRepository.findById(1L)).thenThrow(new RuntimeException("Error de base de datos"));
        
        mockMvc.perform(get("/api/pagos/pedido/1"))
                .andExpect(status().isBadRequest());
    }
}

