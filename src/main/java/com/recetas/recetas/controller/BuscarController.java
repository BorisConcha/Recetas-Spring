package com.recetas.recetas.controller;

import com.recetas.recetas.model.Receta;
import com.recetas.recetas.service.RecetaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.util.HtmlUtils;

import java.util.ArrayList;
import java.util.List;

@Controller
public class BuscarController {

    @Autowired
    private RecetaService recetaService;

    @GetMapping("/buscar")
    public String buscar(
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) String tipoCocina,
            @RequestParam(required = false) String ingrediente,
            @RequestParam(required = false) String paisOrigen,
            @RequestParam(required = false) String dificultad,
            Model model) {

        List<Receta> resultados = new ArrayList<>();

        if (nombre != null && !nombre.isEmpty()) {
            nombre = sanitizeInput(nombre);
            if (isValidInput(nombre)) {
                resultados = recetaService.buscarPorNombre(nombre);
            }
        } else if (tipoCocina != null && !tipoCocina.isEmpty()) {
            tipoCocina = sanitizeInput(tipoCocina);
            if (isValidInput(tipoCocina)) {
                resultados = recetaService.buscarPorTipoCocina(tipoCocina);
            }
        } else if (ingrediente != null && !ingrediente.isEmpty()) {
            ingrediente = sanitizeInput(ingrediente);
            if (isValidInput(ingrediente)) {
                resultados = recetaService.buscarPorIngrediente(ingrediente);
            }
        } else if (paisOrigen != null && !paisOrigen.isEmpty()) {
            paisOrigen = sanitizeInput(paisOrigen);
            if (isValidInput(paisOrigen)) {
                resultados = recetaService.buscarPorPaisOrigen(paisOrigen);
            }
        } else if (dificultad != null && !dificultad.isEmpty()) {
            dificultad = sanitizeInput(dificultad);
            if (isValidInput(dificultad)) {
                resultados = recetaService.buscarPorDificultad(dificultad);
            }
        } else {
            resultados = recetaService.obtenerTodasLasRecetas();
        }

        model.addAttribute("resultados", resultados);
        model.addAttribute("nombre", nombre != null ? HtmlUtils.htmlEscape(nombre) : "");
        model.addAttribute("tipoCocina", tipoCocina != null ? HtmlUtils.htmlEscape(tipoCocina) : "");
        model.addAttribute("ingrediente", ingrediente != null ? HtmlUtils.htmlEscape(ingrediente) : "");
        model.addAttribute("paisOrigen", paisOrigen != null ? HtmlUtils.htmlEscape(paisOrigen) : "");
        model.addAttribute("dificultad", dificultad != null ? HtmlUtils.htmlEscape(dificultad) : "");

        return "buscar";
    }
    
    private String sanitizeInput(String input) {
        if (input == null) {
            return "";
        }

        return input.trim()
                .replaceAll("[<>\"';\\\\]", "")
                .substring(0, Math.min(input.length(), 100));
    }
    

    private boolean isValidInput(String input) {
        if (input == null || input.isEmpty()) {
            return false;
        }
        String lowerInput = input.toLowerCase();
        return !lowerInput.contains("script") &&
               !lowerInput.contains("select") &&
               !lowerInput.contains("drop") &&
               !lowerInput.contains("insert") &&
               !lowerInput.contains("update") &&
               !lowerInput.contains("delete") &&
               !lowerInput.contains("exec") &&
               !lowerInput.contains("union");
    }
}
