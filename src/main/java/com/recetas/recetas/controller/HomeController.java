package com.recetas.recetas.controller;

import com.recetas.recetas.service.AnuncioService;
import com.recetas.recetas.service.ProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
    
    @Autowired
    private ProductoService productoService;

    @Autowired
    private AnuncioService anuncioService;

    @GetMapping({"/", "/inicio"})
    public String inicio(Model model) {
        model.addAttribute("productosRecientes", productoService.obtenerProductosRecientes());
        model.addAttribute("productos", productoService.obtenerProductosActivos());
        model.addAttribute("anuncios", anuncioService.obtenerAnunciosActivos());
        return "inicio";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }
    
    @GetMapping("/registro")
    public String registro() {
        return "registro";
    }
    
    @GetMapping("/recuperar-password")
    public String recuperarPassword() {
        return "recuperar-password";
    }
    
    @GetMapping("/perfil")
    public String perfil() {
        return "perfil";
    }

}
