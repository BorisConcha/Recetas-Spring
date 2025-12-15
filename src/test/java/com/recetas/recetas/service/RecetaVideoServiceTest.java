package com.recetas.recetas.service;

import com.recetas.recetas.model.Receta;
import com.recetas.recetas.model.RecetaVideo;
import com.recetas.recetas.repository.RecetaRepository;
import com.recetas.recetas.repository.RecetaVideoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RecetaVideoServiceTest {

    @Mock
    private RecetaVideoRepository recetaVideoRepository;

    @Mock
    private RecetaRepository recetaRepository;

    @Mock
    private ArchivoService archivoService;

    @InjectMocks
    private RecetaVideoService recetaVideoService;

    private Receta receta;
    private RecetaVideo video;
    private MultipartFile file;

    @BeforeEach
    void setUp() {
        receta = new Receta();
        receta.setId(1L);
        receta.setNombre("Receta Test");

        video = new RecetaVideo();
        video.setId(1L);
        video.setReceta(receta);
        video.setUrlVideo("/uploads/videos/test.mp4");
        video.setNombreArchivo("test.mp4");
        video.setTipoArchivo("video/mp4");
        video.setTamañoArchivo(1024L);

        file = new MockMultipartFile(
                "file",
                "test.mp4",
                "video/mp4",
                "test content".getBytes()
        );
    }

    @Test
    void testSubirVideo() throws IOException {
        when(recetaRepository.findById(1L)).thenReturn(Optional.of(receta));
        when(archivoService.guardarVideo(file)).thenReturn("/uploads/videos/test.mp4");
        when(recetaVideoRepository.save(any(RecetaVideo.class))).thenReturn(video);

        RecetaVideo resultado = recetaVideoService.subirVideo(1L, file);

        assertNotNull(resultado);
        assertEquals("/uploads/videos/test.mp4", resultado.getUrlVideo());
        verify(recetaRepository).findById(1L);
        verify(archivoService).guardarVideo(file);
        verify(recetaVideoRepository).save(any(RecetaVideo.class));
    }

    @Test
    void testSubirVideo_RecetaNoEncontrada() throws IOException {
        when(recetaRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            recetaVideoService.subirVideo(999L, file);
        });

        verify(recetaRepository).findById(999L);
        verify(archivoService, never()).guardarVideo(any());
    }

    @Test
    void testObtenerVideosPorReceta() {
        List<RecetaVideo> videos = Arrays.asList(video);
        when(recetaVideoRepository.findByRecetaIdOrderByFechaSubidaDesc(1L)).thenReturn(videos);

        List<RecetaVideo> resultado = recetaVideoService.obtenerVideosPorReceta(1L);

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        verify(recetaVideoRepository).findByRecetaIdOrderByFechaSubidaDesc(1L);
    }

    @Test
    void testEliminarVideo() {
        doNothing().when(recetaVideoRepository).deleteById(1L);

        assertDoesNotThrow(() -> {
            recetaVideoService.eliminarVideo(1L);
        });

        verify(recetaVideoRepository).deleteById(1L);
    }
}

