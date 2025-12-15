package com.recetas.recetas.dto;

import org.junit.jupiter.api.Test;

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
        ComentarioResponse dto = new ComentarioResponse(1L, 2L, "User", "Comment", null);
        assertEquals(1L, dto.getId());
        assertEquals(2L, dto.getRecetaId());
        assertEquals("User", dto.getUsuarioNombre());
        assertEquals("Comment", dto.getComentario());
        
        dto.setId(3L);
        dto.setRecetaId(4L);
        dto.setUsuarioNombre("NewUser");
        dto.setComentario("NewComment");
        
        assertEquals(3L, dto.getId());
        assertEquals(4L, dto.getRecetaId());
        assertEquals("NewUser", dto.getUsuarioNombre());
        assertEquals("NewComment", dto.getComentario());
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
}

