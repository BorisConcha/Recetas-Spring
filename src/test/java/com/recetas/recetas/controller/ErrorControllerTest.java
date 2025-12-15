package com.recetas.recetas.controller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ErrorControllerTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private Model model;

    @InjectMocks
    private ErrorController errorController;

    @Test
    void testHandleError_404() {
        when(request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE)).thenReturn(HttpStatus.NOT_FOUND.value());
        when(request.getAttribute(RequestDispatcher.ERROR_REQUEST_URI)).thenReturn("/test");

        String viewName = errorController.handleError(request, model);

        assertEquals("error", viewName);
        verify(model).addAttribute("errorCode", "404");
        verify(model).addAttribute("errorMessage", "Página no encontrada");
    }

    @Test
    void testHandleError_403() {
        when(request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE)).thenReturn(HttpStatus.FORBIDDEN.value());
        when(request.getAttribute(RequestDispatcher.ERROR_REQUEST_URI)).thenReturn("/test");

        String viewName = errorController.handleError(request, model);

        assertEquals("error", viewName);
        verify(model).addAttribute("errorCode", "403");
        verify(model).addAttribute("errorMessage", "Acceso denegado");
    }

    @Test
    void testHandleError_500() {
        when(request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE)).thenReturn(HttpStatus.INTERNAL_SERVER_ERROR.value());
        when(request.getAttribute(RequestDispatcher.ERROR_REQUEST_URI)).thenReturn("/test");

        String viewName = errorController.handleError(request, model);

        assertEquals("error", viewName);
        verify(model).addAttribute("errorCode", "500");
        verify(model).addAttribute("errorMessage", "Error interno del servidor");
    }

    @Test
    void testHandleError_SinStatusCode() {
        when(request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE)).thenReturn(null);

        String viewName = errorController.handleError(request, model);

        assertEquals("error", viewName);
        verify(model).addAttribute("errorCode", "500");
        verify(model).addAttribute("errorMessage", "Ha ocurrido un error");
    }

    @Test
    void testAccessDenied() {
        String viewName = errorController.accessDenied(model);

        assertEquals("error", viewName);
        verify(model).addAttribute("errorMessage", "No tienes permisos para acceder a este recurso");
    }

    @Test
    void testHandleException() {
        Exception exception = new RuntimeException("Test exception");

        String viewName = errorController.handleException(exception, model);

        assertEquals("error", viewName);
        verify(model).addAttribute("errorCode", "500");
        verify(model).addAttribute("errorMessage", "Ha ocurrido un error inesperado. Por favor, intente nuevamente.");
    }

    @Test
    void testHandleError_OtroStatusCode() {
        when(request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE)).thenReturn(400);

        String viewName = errorController.handleError(request, model);

        assertEquals("error", viewName);
        verify(model).addAttribute("errorCode", "400");
        verify(model).addAttribute("errorMessage", "Ha ocurrido un error");
    }

    @Test
    void testHandleError_StatusCodeComoString() {
        when(request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE)).thenReturn("500");

        String viewName = errorController.handleError(request, model);

        assertEquals("error", viewName);
        verify(model).addAttribute("errorCode", "500");
    }
}

