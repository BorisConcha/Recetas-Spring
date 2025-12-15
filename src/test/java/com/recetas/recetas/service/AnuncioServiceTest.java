package com.recetas.recetas.service;

import com.recetas.recetas.model.Anuncio;
import com.recetas.recetas.repository.AnuncioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AnuncioServiceTest {

    @Mock
    private AnuncioRepository anuncioRepository;

    @InjectMocks
    private AnuncioService anuncioService;

    private Anuncio anuncio;

    @BeforeEach
    void setUp() {
        anuncio = new Anuncio();
        anuncio.setId(1L);
        anuncio.setActivo(1);
    }

    @Test
    void testObtenerAnunciosActivos() {
        List<Anuncio> anuncios = Arrays.asList(anuncio);
        when(anuncioRepository.findByActivoTrue()).thenReturn(anuncios);

        List<Anuncio> resultado = anuncioService.obtenerAnunciosActivos();

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals(1, resultado.get(0).getActivo());
        verify(anuncioRepository).findByActivoTrue();
    }

    @Test
    void testObtenerAnunciosActivos_Vacio() {
        when(anuncioRepository.findByActivoTrue()).thenReturn(Arrays.asList());

        List<Anuncio> resultado = anuncioService.obtenerAnunciosActivos();

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
        verify(anuncioRepository).findByActivoTrue();
    }
}

