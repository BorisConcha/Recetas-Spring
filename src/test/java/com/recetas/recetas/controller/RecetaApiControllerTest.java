package com.recetas.recetas.controller;

import com.recetas.recetas.dto.RecetaRequest;
import com.recetas.recetas.model.Receta;
import com.recetas.recetas.service.RecetaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RecetaApiControllerTest {

    @Mock
    private RecetaService recetaService;

    @InjectMocks
    private RecetaApiController recetaApiController;

    private RecetaRequest recetaRequest;
    private Receta receta;

    @BeforeEach
    void setUp() {
        recetaRequest = new RecetaRequest();
        recetaRequest.setNombre("Receta Test");
        recetaRequest.setTipoCocina("Italiana");
        recetaRequest.setPaisOrigen("Italia");
        recetaRequest.setDificultad("Media");
        recetaRequest.setTiempoCoccion(30);
        recetaRequest.setIngredientes("Ingredientes test");
        recetaRequest.setInstrucciones("Instrucciones test");

        receta = new Receta();
        receta.setId(1L);
        receta.setNombre("Receta Test");
    }

    @Test
    void testCrearReceta_Exitoso() {
        when(recetaService.guardarReceta(any(Receta.class))).thenReturn(receta);

        ResponseEntity<?> response = recetaApiController.crearReceta(recetaRequest);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(recetaService).guardarReceta(any(Receta.class));
    }

    @Test
    void testCrearReceta_Error() {
        when(recetaService.guardarReceta(any(Receta.class)))
                .thenThrow(new RuntimeException("Error al guardar"));

        ResponseEntity<?> response = recetaApiController.crearReceta(recetaRequest);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(recetaService).guardarReceta(any(Receta.class));
    }
}

