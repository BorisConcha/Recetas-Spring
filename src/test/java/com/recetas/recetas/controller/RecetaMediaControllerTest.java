package com.recetas.recetas.controller;

import com.recetas.recetas.model.RecetaFoto;
import com.recetas.recetas.model.RecetaVideo;
import com.recetas.recetas.service.RecetaFotoService;
import com.recetas.recetas.service.RecetaVideoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RecetaMediaControllerTest {

    @Mock
    private RecetaFotoService recetaFotoService;

    @Mock
    private RecetaVideoService recetaVideoService;

    @InjectMocks
    private RecetaMediaController recetaMediaController;

    private MultipartFile file;
    private RecetaFoto foto;
    private RecetaVideo video;

    @BeforeEach
    void setUp() {
        file = new MockMultipartFile(
                "file",
                "test.jpg",
                "image/jpeg",
                "test content".getBytes()
        );

        foto = new RecetaFoto();
        foto.setId(1L);
        foto.setUrlFoto("/uploads/imagenes/test.jpg");

        video = new RecetaVideo();
        video.setId(1L);
        video.setUrlVideo("/uploads/videos/test.mp4");
    }

    @Test
    void testSubirFoto_Exitoso() throws IOException {
        when(recetaFotoService.subirFoto(1L, file)).thenReturn(foto);

        ResponseEntity<?> response = recetaMediaController.subirFoto(1L, file);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(recetaFotoService).subirFoto(1L, file);
    }

    @Test
    void testSubirFoto_ErrorIOException() throws IOException {
        when(recetaFotoService.subirFoto(1L, file))
                .thenThrow(new IOException("Error al guardar archivo"));

        ResponseEntity<?> response = recetaMediaController.subirFoto(1L, file);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().toString().contains("Error al subir foto"));
    }

    @Test
    void testSubirFoto_ErrorGeneral() throws IOException {
        when(recetaFotoService.subirFoto(1L, file))
                .thenThrow(new RuntimeException("Error general"));

        ResponseEntity<?> response = recetaMediaController.subirFoto(1L, file);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void testObtenerFotos() {
        List<RecetaFoto> fotos = Arrays.asList(foto);
        when(recetaFotoService.obtenerFotosPorReceta(1L)).thenReturn(fotos);

        ResponseEntity<List<RecetaFoto>> response = recetaMediaController.obtenerFotos(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        verify(recetaFotoService).obtenerFotosPorReceta(1L);
    }

    @Test
    void testEliminarFoto_Exitoso() {
        doNothing().when(recetaFotoService).eliminarFoto(1L);

        ResponseEntity<?> response = recetaMediaController.eliminarFoto(1L, 1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(recetaFotoService).eliminarFoto(1L);
    }

    @Test
    void testEliminarFoto_Error() {
        doThrow(new RuntimeException("Error")).when(recetaFotoService).eliminarFoto(1L);

        ResponseEntity<?> response = recetaMediaController.eliminarFoto(1L, 1L);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void testSubirVideo_Exitoso() throws IOException {
        when(recetaVideoService.subirVideo(1L, file)).thenReturn(video);

        ResponseEntity<?> response = recetaMediaController.subirVideo(1L, file);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(recetaVideoService).subirVideo(1L, file);
    }

    @Test
    void testSubirVideo_ErrorIOException() throws IOException {
        when(recetaVideoService.subirVideo(1L, file))
                .thenThrow(new IOException("Error al guardar archivo"));

        ResponseEntity<?> response = recetaMediaController.subirVideo(1L, file);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().toString().contains("Error al subir video"));
    }

    @Test
    void testObtenerVideos() {
        List<RecetaVideo> videos = Arrays.asList(video);
        when(recetaVideoService.obtenerVideosPorReceta(1L)).thenReturn(videos);

        ResponseEntity<List<RecetaVideo>> response = recetaMediaController.obtenerVideos(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        verify(recetaVideoService).obtenerVideosPorReceta(1L);
    }

    @Test
    void testEliminarVideo_Exitoso() {
        doNothing().when(recetaVideoService).eliminarVideo(1L);

        ResponseEntity<?> response = recetaMediaController.eliminarVideo(1L, 1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(recetaVideoService).eliminarVideo(1L);
    }

    @Test
    void testEliminarVideo_Error() {
        doThrow(new RuntimeException("Error")).when(recetaVideoService).eliminarVideo(1L);

        ResponseEntity<?> response = recetaMediaController.eliminarVideo(1L, 1L);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void testObtenerFotos_Vacio() {
        when(recetaFotoService.obtenerFotosPorReceta(1L)).thenReturn(Arrays.asList());

        ResponseEntity<List<RecetaFoto>> response = recetaMediaController.obtenerFotos(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isEmpty());
    }

    @Test
    void testObtenerVideos_Vacio() {
        when(recetaVideoService.obtenerVideosPorReceta(1L)).thenReturn(Arrays.asList());

        ResponseEntity<List<RecetaVideo>> response = recetaMediaController.obtenerVideos(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isEmpty());
    }
}

