package com.recetas.recetas.controller;

import com.recetas.recetas.model.Receta;
import com.recetas.recetas.service.RecetaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.Model;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RecetaControllerTest {

    @Mock
    private RecetaService recetaService;

    @Mock
    private Model model;

    @InjectMocks
    private RecetaController recetaController;

    private Receta receta;

    @BeforeEach
    void setUp() {
        receta = new Receta();
        receta.setId(1L);
        receta.setNombre("Receta Test");
    }

    @Test
    void testVerDetalleReceta_Exitoso() {
        when(recetaService.obtenerRecetaPorId(1L)).thenReturn(Optional.of(receta));

        String viewName = recetaController.verDetalleReceta(1L, model);

        assertEquals("detalle-receta", viewName);
        verify(recetaService).obtenerRecetaPorId(1L);
        verify(model).addAttribute("receta", receta);
    }

    @Test
    void testVerDetalleReceta_IDInvalido_Null() {
        String viewName = recetaController.verDetalleReceta(null, model);

        assertEquals("redirect:/buscar?error=id_invalido", viewName);
        verify(recetaService, never()).obtenerRecetaPorId(any());
    }

    @Test
    void testVerDetalleReceta_IDInvalido_Negativo() {
        String viewName = recetaController.verDetalleReceta(-1L, model);

        assertEquals("redirect:/buscar?error=id_invalido", viewName);
        verify(recetaService, never()).obtenerRecetaPorId(any());
    }

    @Test
    void testVerDetalleReceta_RecetaNoEncontrada() {
        when(recetaService.obtenerRecetaPorId(999L)).thenReturn(Optional.empty());

        String viewName = recetaController.verDetalleReceta(999L, model);

        assertEquals("redirect:/buscar?error=receta_no_encontrada", viewName);
        verify(recetaService).obtenerRecetaPorId(999L);
    }

    @Test
    void testVerDetalleReceta_Error() {
        when(recetaService.obtenerRecetaPorId(1L))
                .thenThrow(new RuntimeException("Error de base de datos"));

        String viewName = recetaController.verDetalleReceta(1L, model);

        assertEquals("redirect:/buscar?error=receta_no_encontrada", viewName);
        verify(recetaService).obtenerRecetaPorId(1L);
    }

    @Test
    void testVerDetalleReceta_IDCero() {
        String viewName = recetaController.verDetalleReceta(0L, model);

        assertEquals("redirect:/buscar?error=id_invalido", viewName);
        verify(recetaService, never()).obtenerRecetaPorId(any());
    }
}

