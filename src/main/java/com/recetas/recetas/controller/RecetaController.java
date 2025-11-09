package com.recetas.recetas.controller;


import com.recetas.recetas.model.Receta;
import com.recetas.recetas.service.RecetaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
@RequestMapping("/recetas")
public class RecetaController {
    
    private static final Logger logger = LoggerFactory.getLogger(RecetaController.class);
    
    @Autowired
    private RecetaService recetaService;


    @GetMapping("/{id}")
    public String verDetalleReceta(@PathVariable Long id, Model model) {
        if (id == null || id <= 0) {
            logger.warn("Intento de acceso con ID inválido: {}", id);
            return "redirect:/buscar?error=id_invalido";
        }
        
        try {
            Receta receta = recetaService.obtenerRecetaPorId(id)
                    .orElseThrow(() -> new RuntimeException("Receta no encontrada con ID: " + id));
            
            model.addAttribute("receta", receta);
            
            logger.info("Usuario accedió a receta ID: {}", id);
            
            return "detalle-receta";
        } catch (Exception e) {
            logger.error("Error al obtener receta ID {}: {}", id, e.getMessage());
            return "redirect:/buscar?error=receta_no_encontrada";
        }
    }
}
