package com.recetas.recetas.dto;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class DtoTest {

    @Test
    void testComentarioRequest() {
        ComentarioRequest dto = new ComentarioRequest();
        dto.setRecetaId(1L);
        dto.setComentario("Test");
        
        assertEquals(1L, dto.getRecetaId());
        assertEquals("Test", dto.getComentario());
    }

    @Test
    void testComentarioResponse() {
        java.time.LocalDateTime fecha = java.time.LocalDateTime.now();
        ComentarioResponse dto = new ComentarioResponse(1L, 2L, "User", "Comment", fecha);
        assertEquals(1L, dto.getId());
        assertEquals(2L, dto.getRecetaId());
        assertEquals("User", dto.getUsuarioNombre());
        assertEquals("Comment", dto.getComentario());
        assertEquals(fecha, dto.getFechaCreacion());
        
        dto.setId(3L);
        dto.setRecetaId(4L);
        dto.setUsuarioNombre("NewUser");
        dto.setComentario("NewComment");
        java.time.LocalDateTime nuevaFecha = java.time.LocalDateTime.now();
        dto.setFechaCreacion(nuevaFecha);
        
        assertEquals(3L, dto.getId());
        assertEquals(4L, dto.getRecetaId());
        assertEquals("NewUser", dto.getUsuarioNombre());
        assertEquals("NewComment", dto.getComentario());
        assertEquals(nuevaFecha, dto.getFechaCreacion());
        
        // Test constructor sin parámetros
        ComentarioResponse dto2 = new ComentarioResponse();
        assertNull(dto2.getId());
        assertNull(dto2.getRecetaId());
        dto2.setFechaCreacion(fecha);
        assertEquals(fecha, dto2.getFechaCreacion());
    }

    @Test
    void testLoginRequest() {
        LoginRequest dto = new LoginRequest();
        dto.setUsername("user");
        dto.setPassword("pass");
        
        assertEquals("user", dto.getUsername());
        assertEquals("pass", dto.getPassword());
        
        LoginRequest dto2 = new LoginRequest("user2", "pass2");
        assertEquals("user2", dto2.getUsername());
        assertEquals("pass2", dto2.getPassword());
    }

    @Test
    void testLoginResponse() {
        LoginResponse dto = new LoginResponse();
        dto.setToken("token");
        dto.setType("Bearer");
        dto.setUsername("user");
        
        assertEquals("token", dto.getToken());
        assertEquals("Bearer", dto.getType());
        assertEquals("user", dto.getUsername());
        
        LoginResponse dto2 = new LoginResponse("token2", "user2");
        assertEquals("token2", dto2.getToken());
        assertEquals("Bearer", dto2.getType());
        assertEquals("user2", dto2.getUsername());
    }

    @Test
    void testRecetaRequest() {
        RecetaRequest dto = new RecetaRequest();
        dto.setNombre("Receta");
        dto.setIngredientes("Ing");
        dto.setInstrucciones("Inst");
        dto.setTipoCocina("Tipo");
        dto.setPaisOrigen("Pais");
        dto.setTiempoCoccion(30);
        dto.setDificultad("Media");
        
        assertEquals("Receta", dto.getNombre());
        assertEquals("Ing", dto.getIngredientes());
        assertEquals("Inst", dto.getInstrucciones());
        assertEquals("Tipo", dto.getTipoCocina());
        assertEquals("Pais", dto.getPaisOrigen());
        assertEquals(30, dto.getTiempoCoccion());
        assertEquals("Media", dto.getDificultad());
    }

    @Test
    void testRegistroRequest() {
        RegistroRequest dto = new RegistroRequest();
        dto.setNombreCompleto("Name");
        dto.setUsername("user");
        dto.setEmail("email@test.com");
        dto.setPassword("pass");
        
        assertEquals("Name", dto.getNombreCompleto());
        assertEquals("user", dto.getUsername());
        assertEquals("email@test.com", dto.getEmail());
        assertEquals("pass", dto.getPassword());
        
        RegistroRequest dto2 = new RegistroRequest("Name2", "user2", "email2@test.com", "pass2");
        assertEquals("Name2", dto2.getNombreCompleto());
        assertEquals("user2", dto2.getUsername());
        assertEquals("email2@test.com", dto2.getEmail());
        assertEquals("pass2", dto2.getPassword());
    }

    @Test
    void testValoracionRequest() {
        ValoracionRequest dto = new ValoracionRequest();
        dto.setRecetaId(1L);
        dto.setPuntuacion(5);
        
        assertEquals(1L, dto.getRecetaId());
        assertEquals(5, dto.getPuntuacion());
    }

    @Test
    void testValoracionResponse() {
        ValoracionResponse dto = new ValoracionResponse();
        dto.setPromedio(4.5);
        dto.setTotalValoraciones(10L);
        dto.setMiValoracion(5);
        
        assertEquals(4.5, dto.getPromedio());
        assertEquals(10L, dto.getTotalValoraciones());
        assertEquals(5, dto.getMiValoracion());
        
        ValoracionResponse dto2 = new ValoracionResponse(3.5, 5L, 4);
        assertEquals(3.5, dto2.getPromedio());
        assertEquals(5L, dto2.getTotalValoraciones());
        assertEquals(4, dto2.getMiValoracion());
    }

    @Test
    void testCompartirRequest() {
        CompartirRequest dto = new CompartirRequest();
        dto.setRecetaId(1L);
        dto.setPlataforma("Facebook");
        
        assertEquals(1L, dto.getRecetaId());
        assertEquals("Facebook", dto.getPlataforma());
    }
    
    @Test
    void testProductoRequest() {
        ProductoRequest dto = new ProductoRequest();
        dto.setNombre("Laptop");
        dto.setDescripcion("Laptop HP");
        dto.setPrecio(new java.math.BigDecimal("599.99"));
        dto.setStock(10);
        dto.setCategoria("Electrónica");
        dto.setImagenUrl("http://example.com/image.jpg");
        
        assertEquals("Laptop", dto.getNombre());
        assertEquals("Laptop HP", dto.getDescripcion());
        assertEquals(new java.math.BigDecimal("599.99"), dto.getPrecio());
        assertEquals(10, dto.getStock());
        assertEquals("Electrónica", dto.getCategoria());
        assertEquals("http://example.com/image.jpg", dto.getImagenUrl());
    }
    
    @Test
    void testPedidoRequest() {
        PedidoRequest dto = new PedidoRequest();
        dto.setDireccionEntrega("Calle 123");
        dto.setTelefonoContacto("123456789");
        
        PedidoRequest.DetallePedidoRequest detalle = new PedidoRequest.DetallePedidoRequest();
        detalle.setProductoId(1L);
        detalle.setCantidad(2);
        dto.setDetalles(java.util.Arrays.asList(detalle));
        
        assertEquals("Calle 123", dto.getDireccionEntrega());
        assertEquals("123456789", dto.getTelefonoContacto());
        assertEquals(1, dto.getDetalles().size());
        assertEquals(1L, dto.getDetalles().get(0).getProductoId());
        assertEquals(2, dto.getDetalles().get(0).getCantidad());
    }
    
    @Test
    void testActualizarPerfilRequest() {
        ActualizarPerfilRequest dto = new ActualizarPerfilRequest();
        dto.setNombreCompleto("Nombre Completo");
        dto.setEmail("email@test.com");
        
        assertEquals("Nombre Completo", dto.getNombreCompleto());
        assertEquals("email@test.com", dto.getEmail());
    }
    
    @Test
    void testCambiarPasswordRequest() {
        CambiarPasswordRequest dto = new CambiarPasswordRequest();
        dto.setPasswordActual("oldPass");
        dto.setNuevaPassword("newPass123!");
        
        assertEquals("oldPass", dto.getPasswordActual());
        assertEquals("newPass123!", dto.getNuevaPassword());
        
        var result = dto.validateNuevaPassword();
        assertTrue(result.isValid());
        
        dto.setNuevaPassword("weak");
        var result2 = dto.validateNuevaPassword();
        assertFalse(result2.isValid());
    }
    
    @Test
    void testRecuperarPasswordRequest() {
        RecuperarPasswordRequest dto = new RecuperarPasswordRequest();
        dto.setEmail("email@test.com");
        
        assertEquals("email@test.com", dto.getEmail());
    }
    
    @Test
    void testRegistroRequest_validatePassword() {
        RegistroRequest dto = new RegistroRequest();
        dto.setPassword("ValidPass123!");
        
        var result = dto.validatePassword();
        assertTrue(result.isValid());
        
        dto.setPassword("weak");
        var result2 = dto.validatePassword();
        assertFalse(result2.isValid());
    }
}

