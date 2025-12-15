package com.recetas.recetas.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ArchivoServiceTest {

    @InjectMocks
    private ArchivoService archivoService;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(archivoService, "uploadDir", tempDir.toString());
    }

    @Test
    void testGuardarImagen_Valido() throws IOException {
        byte[] content = "test image content".getBytes();
        MultipartFile file = new MockMultipartFile(
                "file",
                "test.jpg",
                "image/jpeg",
                content
        );

        String resultado = archivoService.guardarImagen(file);

        assertNotNull(resultado);
        assertTrue(resultado.contains("imagenes"));
        assertTrue(Files.exists(tempDir.resolve("imagenes").resolve(resultado.substring(resultado.lastIndexOf("/") + 1))));
    }

    @Test
    void testGuardarVideo_Valido() throws IOException {
        byte[] content = "test video content".getBytes();
        MultipartFile file = new MockMultipartFile(
                "file",
                "test.mp4",
                "video/mp4",
                content
        );

        String resultado = archivoService.guardarVideo(file);

        assertNotNull(resultado);
        assertTrue(resultado.contains("videos"));
        assertTrue(Files.exists(tempDir.resolve("videos").resolve(resultado.substring(resultado.lastIndexOf("/") + 1))));
    }

    @Test
    void testGuardarImagen_ArchivoVacio() {
        MultipartFile file = new MockMultipartFile(
                "file",
                "test.jpg",
                "image/jpeg",
                new byte[0]
        );

        assertThrows(IOException.class, () -> {
            archivoService.guardarImagen(file);
        });
    }

    @Test
    void testGuardarImagen_TipoNoPermitido() {
        byte[] content = "test content".getBytes();
        MultipartFile file = new MockMultipartFile(
                "file",
                "test.txt",
                "text/plain",
                content
        );

        assertThrows(IOException.class, () -> {
            archivoService.guardarImagen(file);
        });
    }

    @Test
    void testGuardarVideo_TipoNoPermitido() {
        byte[] content = "test content".getBytes();
        MultipartFile file = new MockMultipartFile(
                "file",
                "test.txt",
                "text/plain",
                content
        );

        assertThrows(IOException.class, () -> {
            archivoService.guardarVideo(file);
        });
    }

    @Test
    void testGuardarImagen_ArchivoGrande() {
        byte[] largeContent = new byte[11 * 1024 * 1024]; // 11MB
        MultipartFile file = new MockMultipartFile(
                "file",
                "large.jpg",
                "image/jpeg",
                largeContent
        );

        assertThrows(IOException.class, () -> {
            archivoService.guardarImagen(file);
        });
    }

    @Test
    void testGuardarImagen_PNG() throws IOException {
        byte[] content = "test image content".getBytes();
        MultipartFile file = new MockMultipartFile(
                "file",
                "test.png",
                "image/png",
                content
        );

        String resultado = archivoService.guardarImagen(file);
        assertNotNull(resultado);
    }

    @Test
    void testGuardarVideo_WebM() throws IOException {
        byte[] content = "test video content".getBytes();
        MultipartFile file = new MockMultipartFile(
                "file",
                "test.webm",
                "video/webm",
                content
        );

        String resultado = archivoService.guardarVideo(file);
        assertNotNull(resultado);
    }

    @Test
    void testGuardarImagen_GIF() throws IOException {
        byte[] content = "test image content".getBytes();
        MultipartFile file = new MockMultipartFile(
                "file",
                "test.gif",
                "image/gif",
                content
        );

        String resultado = archivoService.guardarImagen(file);
        assertNotNull(resultado);
    }

    @Test
    void testGuardarImagen_WebP() throws IOException {
        byte[] content = "test image content".getBytes();
        MultipartFile file = new MockMultipartFile(
                "file",
                "test.webp",
                "image/webp",
                content
        );

        String resultado = archivoService.guardarImagen(file);
        assertNotNull(resultado);
    }

    @Test
    void testGuardarVideo_QuickTime() throws IOException {
        byte[] content = "test video content".getBytes();
        MultipartFile file = new MockMultipartFile(
                "file",
                "test.mov",
                "video/quicktime",
                content
        );

        String resultado = archivoService.guardarVideo(file);
        assertNotNull(resultado);
    }

    @Test
    void testGuardarImagen_SinExtension() throws IOException {
        byte[] content = "test image content".getBytes();
        MultipartFile file = new MockMultipartFile(
                "file",
                "test",
                "image/jpeg",
                content
        );

        String resultado = archivoService.guardarImagen(file);
        assertNotNull(resultado);
    }

    @Test
    void testGuardarVideo_SinExtension() throws IOException {
        byte[] content = "test video content".getBytes();
        MultipartFile file = new MockMultipartFile(
                "file",
                "test",
                "video/mp4",
                content
        );

        String resultado = archivoService.guardarVideo(file);
        assertNotNull(resultado);
    }

    @Test
    void testGuardarImagen_NombreArchivoNull() throws IOException {
        byte[] content = "test image content".getBytes();
        MultipartFile file = new MockMultipartFile(
                "file",
                null,
                "image/jpeg",
                content
        );

        String resultado = archivoService.guardarImagen(file);
        assertNotNull(resultado);
    }

    @Test
    void testGuardarVideo_NombreArchivoNull() throws IOException {
        byte[] content = "test video content".getBytes();
        MultipartFile file = new MockMultipartFile(
                "file",
                null,
                "video/mp4",
                content
        );

        String resultado = archivoService.guardarVideo(file);
        assertNotNull(resultado);
    }

    @Test
    void testGuardarImagen_ArchivoExacto10MB() throws IOException {
        byte[] content = new byte[10 * 1024 * 1024]; // Exactamente 10MB
        MultipartFile file = new MockMultipartFile(
                "file",
                "test.jpg",
                "image/jpeg",
                content
        );

        String resultado = archivoService.guardarImagen(file);
        assertNotNull(resultado);
    }
}

