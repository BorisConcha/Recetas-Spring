package com.recetas.recetas.controller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
@ControllerAdvice
public class ErrorController implements org.springframework.boot.web.servlet.error.ErrorController {
    
    private static final Logger logger = LoggerFactory.getLogger(ErrorController.class);


    @RequestMapping("/error")
    public String handleError(HttpServletRequest request, Model model) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        
        String errorMessage = "Ha ocurrido un error";
        String errorCode = "500";
        
        if (status != null) {
            Integer statusCode = Integer.valueOf(status.toString());
            errorCode = statusCode.toString();
            
            if (statusCode == HttpStatus.NOT_FOUND.value()) {
                errorMessage = "Página no encontrada";
                logger.warn("Error 404: Página no encontrada - URI: {}", 
                    request.getAttribute(RequestDispatcher.ERROR_REQUEST_URI));
            } else if (statusCode == HttpStatus.FORBIDDEN.value()) {
                errorMessage = "Acceso denegado";
                logger.warn("Error 403: Acceso denegado - URI: {}", 
                    request.getAttribute(RequestDispatcher.ERROR_REQUEST_URI));
            } else if (statusCode == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
                errorMessage = "Error interno del servidor";
                logger.error("Error 500: Error interno del servidor - URI: {}", 
                    request.getAttribute(RequestDispatcher.ERROR_REQUEST_URI));
            }
        }
        
        model.addAttribute("errorCode", errorCode);
        model.addAttribute("errorMessage", errorMessage);
        
        return "error";
    }
    
    @RequestMapping("/acceso-denegado")
    public String accessDenied(Model model) {
        logger.warn("Intento de acceso no autorizado");
        model.addAttribute("errorMessage", "No tienes permisos para acceder a este recurso");
        return "error";
    }
    

    @ExceptionHandler(Exception.class)
    public String handleException(Exception e, Model model) {

        logger.error("Excepción no capturada: {}", e.getMessage(), e);
        
        model.addAttribute("errorCode", "500");
        model.addAttribute("errorMessage", "Ha ocurrido un error inesperado. Por favor, intente nuevamente.");
        
        return "error";
    }
}

