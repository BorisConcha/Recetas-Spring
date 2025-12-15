package com.recetas.recetas.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class ModelTest {

    @Test
    void testReceta() {
        Receta receta = new Receta();
        receta.setId(1L);
        receta.setNombre("Test");
        receta.setTipoCocina("Italiana");
        receta.setPaisOrigen("Italia");
        receta.setDificultad("Media");
        receta.setTiempoCoccion(30);
        receta.setDescripcion("Desc");
        receta.setIngredientes("Ing");
        receta.setInstrucciones("Inst");
        receta.setImagenUrl("url");
        receta.setFechaCreacion(LocalDateTime.now());
        receta.setPopularidad(10);
        
        assertEquals(1L, receta.getId());
        assertEquals("Test", receta.getNombre());
        assertEquals("Italiana", receta.getTipoCocina());
        assertEquals("Italia", receta.getPaisOrigen());
        assertEquals("Media", receta.getDificultad());
        assertEquals(30, receta.getTiempoCoccion());
        assertEquals("Desc", receta.getDescripcion());
        assertEquals("Ing", receta.getIngredientes());
        assertEquals("Inst", receta.getInstrucciones());
        assertEquals("url", receta.getImagenUrl());
        assertNotNull(receta.getFechaCreacion());
        assertEquals(10, receta.getPopularidad());
    }

    @Test
    void testUsuario() {
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNombreCompleto("Test User");
        usuario.setUsername("testuser");
        usuario.setPassword("pass");
        usuario.setEmail("test@test.com");
        usuario.setEnabled(1);
        
        assertEquals(1L, usuario.getId());
        assertEquals("Test User", usuario.getNombreCompleto());
        assertEquals("testuser", usuario.getUsername());
        assertEquals("pass", usuario.getPassword());
        assertEquals("test@test.com", usuario.getEmail());
        assertEquals(1, usuario.getEnabled());
        assertTrue(usuario.isEnabled());
        
        usuario.setEnabledBoolean(false);
        assertEquals(0, usuario.getEnabled());
        assertFalse(usuario.isEnabled());
        
        usuario.setEnabledBoolean(true);
        assertEquals(1, usuario.getEnabled());
        assertTrue(usuario.isEnabled());
    }

    @Test
    void testComentario() {
        Comentario comentario = new Comentario();
        comentario.setId(1L);
        comentario.setComentario("Test");
        comentario.setFechaCreacion(LocalDateTime.now());
        
        assertEquals(1L, comentario.getId());
        assertEquals("Test", comentario.getComentario());
        assertNotNull(comentario.getFechaCreacion());
    }

    @Test
    void testValoracion() {
        Valoracion valoracion = new Valoracion();
        valoracion.setId(1L);
        valoracion.setPuntuacion(5);
        valoracion.setFechaCreacion(LocalDateTime.now());
        valoracion.setFechaActualizacion(LocalDateTime.now());
        
        assertEquals(1L, valoracion.getId());
        assertEquals(5, valoracion.getPuntuacion());
        assertNotNull(valoracion.getFechaCreacion());
        assertNotNull(valoracion.getFechaActualizacion());
    }

    @Test
    void testRecetaFoto() {
        RecetaFoto foto = new RecetaFoto();
        foto.setId(1L);
        foto.setUrlFoto("url");
        foto.setNombreArchivo("file.jpg");
        foto.setTipoArchivo("image/jpeg");
        foto.setTamañoArchivo(1024L);
        foto.setFechaSubida(LocalDateTime.now());
        foto.setEsPrincipal(true);
        
        assertEquals(1L, foto.getId());
        assertEquals("url", foto.getUrlFoto());
        assertEquals("file.jpg", foto.getNombreArchivo());
        assertEquals("image/jpeg", foto.getTipoArchivo());
        assertEquals(1024L, foto.getTamañoArchivo());
        assertNotNull(foto.getFechaSubida());
        assertTrue(foto.getEsPrincipal());
    }

    @Test
    void testRecetaVideo() {
        RecetaVideo video = new RecetaVideo();
        video.setId(1L);
        video.setUrlVideo("url");
        video.setNombreArchivo("file.mp4");
        video.setTipoArchivo("video/mp4");
        video.setTamañoArchivo(2048L);
        video.setDuracionSegundos(60);
        video.setFechaSubida(LocalDateTime.now());
        
        assertEquals(1L, video.getId());
        assertEquals("url", video.getUrlVideo());
        assertEquals("file.mp4", video.getNombreArchivo());
        assertEquals("video/mp4", video.getTipoArchivo());
        assertEquals(2048L, video.getTamañoArchivo());
        assertEquals(60, video.getDuracionSegundos());
        assertNotNull(video.getFechaSubida());
    }

    @Test
    void testAnuncio() {
        Anuncio anuncio = new Anuncio();
        anuncio.setId(1L);
        anuncio.setEmpresa("Empresa");
        anuncio.setTitulo("Titulo");
        anuncio.setDescripcion("Desc");
        anuncio.setImagenUrl("url");
        anuncio.setUrlDestino("dest");
        anuncio.setActivo(1);
        
        assertEquals(1L, anuncio.getId());
        assertEquals("Empresa", anuncio.getEmpresa());
        assertEquals("Titulo", anuncio.getTitulo());
        assertEquals("Desc", anuncio.getDescripcion());
        assertEquals("url", anuncio.getImagenUrl());
        assertEquals("dest", anuncio.getUrlDestino());
        assertEquals(1, anuncio.getActivo());
        assertTrue(anuncio.isActivo());
        
        anuncio.setActivoBoolean(false);
        assertEquals(0, anuncio.getActivo());
        assertFalse(anuncio.isActivo());
        
        anuncio.setActivoBoolean(true);
        assertEquals(1, anuncio.getActivo());
        assertTrue(anuncio.isActivo());
    }

    @Test
    void testRole() {
        Role role = new Role();
        role.setId(1L);
        role.setNombre("ROLE_USER");
        
        assertEquals(1L, role.getId());
        assertEquals("ROLE_USER", role.getNombre());
    }

    @Test
    void testRecetaCompartida() {
        RecetaCompartida compartida = new RecetaCompartida();
        compartida.setId(1L);
        compartida.setPlataforma("Facebook");
        compartida.setFechaCompartido(LocalDateTime.now());
        
        assertEquals(1L, compartida.getId());
        assertEquals("Facebook", compartida.getPlataforma());
        assertNotNull(compartida.getFechaCompartido());
    }
}

