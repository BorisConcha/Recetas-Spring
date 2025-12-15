package com.recetas.recetas.service;

import com.recetas.recetas.model.Receta;
import com.recetas.recetas.model.RecetaFoto;
import com.recetas.recetas.repository.RecetaFotoRepository;
import com.recetas.recetas.repository.RecetaRepository;
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
class RecetaFotoServiceTest {

    @Mock
    private RecetaFotoRepository recetaFotoRepository;

    @Mock
    private RecetaRepository recetaRepository;

    @Mock
    private ArchivoService archivoService;

    @InjectMocks
    private RecetaFotoService recetaFotoService;

    private Receta receta;
    private RecetaFoto foto;
    private MultipartFile file;

    @BeforeEach
    void setUp() {
        receta = new Receta();
        receta.setId(1L);
        receta.setNombre("Receta Test");

        foto = new RecetaFoto();
        foto.setId(1L);
        foto.setReceta(receta);
        foto.setUrlFoto("/uploads/imagenes/test.jpg");
        foto.setNombreArchivo("test.jpg");
        foto.setTipoArchivo("image/jpeg");
        foto.setTamañoArchivo(1024L);
        foto.setEsPrincipal(false);

        file = new MockMultipartFile(
                "file",
                "test.jpg",
                "image/jpeg",
                "test content".getBytes()
        );
    }

    @Test
    void testSubirFoto() throws IOException {
        when(recetaRepository.findById(1L)).thenReturn(Optional.of(receta));
        when(archivoService.guardarImagen(file)).thenReturn("/uploads/imagenes/test.jpg");
        when(recetaFotoRepository.findByRecetaIdOrderByFechaSubidaDesc(1L)).thenReturn(Arrays.asList());
        when(recetaFotoRepository.save(any(RecetaFoto.class))).thenReturn(foto);

        RecetaFoto resultado = recetaFotoService.subirFoto(1L, file);

        assertNotNull(resultado);
        verify(recetaRepository).findById(1L);
        verify(archivoService).guardarImagen(file);
        verify(recetaFotoRepository).save(any(RecetaFoto.class));
    }

    @Test
    void testSubirFoto_RecetaNoEncontrada() throws IOException {
        when(recetaRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            recetaFotoService.subirFoto(999L, file);
        });

        verify(recetaRepository).findById(999L);
        verify(archivoService, never()).guardarImagen(any());
    }

    @Test
    void testSubirFoto_PrimeraFoto() throws IOException {
        when(recetaRepository.findById(1L)).thenReturn(Optional.of(receta));
        when(archivoService.guardarImagen(file)).thenReturn("/uploads/imagenes/test.jpg");
        when(recetaFotoRepository.findByRecetaIdOrderByFechaSubidaDesc(1L)).thenReturn(Arrays.asList());
        when(recetaFotoRepository.save(any(RecetaFoto.class))).thenAnswer(invocation -> {
            RecetaFoto f = invocation.getArgument(0);
            f.setEsPrincipal(true);
            return f;
        });

        RecetaFoto resultado = recetaFotoService.subirFoto(1L, file);

        assertNotNull(resultado);
        verify(recetaFotoRepository).save(any(RecetaFoto.class));
    }

    @Test
    void testObtenerFotosPorReceta() {
        List<RecetaFoto> fotos = Arrays.asList(foto);
        when(recetaFotoRepository.findByRecetaIdOrderByFechaSubidaDesc(1L)).thenReturn(fotos);

        List<RecetaFoto> resultado = recetaFotoService.obtenerFotosPorReceta(1L);

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        verify(recetaFotoRepository).findByRecetaIdOrderByFechaSubidaDesc(1L);
    }

    @Test
    void testEliminarFoto() {
        doNothing().when(recetaFotoRepository).deleteById(1L);

        assertDoesNotThrow(() -> {
            recetaFotoService.eliminarFoto(1L);
        });

        verify(recetaFotoRepository).deleteById(1L);
    }

    @Test
    void testMarcarComoPrincipal() {
        RecetaFoto foto1 = new RecetaFoto();
        foto1.setId(1L);
        foto1.setEsPrincipal(true);
        
        RecetaFoto foto2 = new RecetaFoto();
        foto2.setId(2L);
        foto2.setEsPrincipal(false);

        List<RecetaFoto> fotos = Arrays.asList(foto1, foto2);
        when(recetaFotoRepository.findByRecetaIdOrderByFechaSubidaDesc(1L)).thenReturn(fotos);
        when(recetaFotoRepository.findById(2L)).thenReturn(Optional.of(foto2));
        when(recetaFotoRepository.saveAll(anyList())).thenReturn(fotos);
        when(recetaFotoRepository.save(foto2)).thenReturn(foto2);

        recetaFotoService.marcarComoPrincipal(2L, 1L);

        verify(recetaFotoRepository).findByRecetaIdOrderByFechaSubidaDesc(1L);
        verify(recetaFotoRepository).saveAll(anyList());
        verify(recetaFotoRepository).findById(2L);
        verify(recetaFotoRepository).save(foto2);
    }

    @Test
    void testMarcarComoPrincipal_FotoNoEncontrada() {
        List<RecetaFoto> fotos = Arrays.asList(foto);
        when(recetaFotoRepository.findByRecetaIdOrderByFechaSubidaDesc(1L)).thenReturn(fotos);
        when(recetaFotoRepository.findById(999L)).thenReturn(Optional.empty());
        when(recetaFotoRepository.saveAll(anyList())).thenReturn(fotos);

        recetaFotoService.marcarComoPrincipal(999L, 1L);

        verify(recetaFotoRepository).findById(999L);
        verify(recetaFotoRepository, never()).save(any(RecetaFoto.class));
    }

    @Test
    void testSubirFoto_ConFotosExistentes() throws IOException {
        RecetaFoto fotoExistente = new RecetaFoto();
        fotoExistente.setEsPrincipal(false);
        List<RecetaFoto> fotosExistentes = Arrays.asList(fotoExistente);
        
        when(recetaRepository.findById(1L)).thenReturn(Optional.of(receta));
        when(archivoService.guardarImagen(file)).thenReturn("/uploads/imagenes/test.jpg");
        when(recetaFotoRepository.findByRecetaIdOrderByFechaSubidaDesc(1L)).thenReturn(fotosExistentes);
        when(recetaFotoRepository.save(any(RecetaFoto.class))).thenReturn(foto);

        RecetaFoto resultado = recetaFotoService.subirFoto(1L, file);

        assertNotNull(resultado);
        verify(recetaFotoRepository).save(any(RecetaFoto.class));
    }
}

