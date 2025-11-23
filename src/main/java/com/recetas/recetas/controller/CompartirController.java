package com.recetas.recetas.controller;

import com.recetas.recetas.dto.CompartirRequest;
import com.recetas.recetas.model.Usuario;
import com.recetas.recetas.repository.UsuarioRepository;
import com.recetas.recetas.service.CompartirService;
import com.recetas.recetas.util.SecurityUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;


@RestController
@RequestMapping("/api/compartir")
public class CompartirController {
    
    @Autowired
    private CompartirService compartirService;
    
    @Autowired
    private UsuarioRepository usuarioRepository;
    
    @Value("${app.base-url:http://localhost:8080}")
    private String baseUrl;
    

    @PostMapping
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<?> compartirReceta(@Valid @RequestBody CompartirRequest request) {
        try {
            String username = SecurityUtil.getCurrentUsername();
            if (username == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Usuario no autenticado");
            }
            
            Usuario usuario = usuarioRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
            
            compartirService.registrarCompartido(
                    request.getRecetaId(),
                    usuario.getId(),
                    request.getPlataforma()
            );
            
            return ResponseEntity.ok("Receta compartida exitosamente");
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error al compartir receta: " + e.getMessage());
        }
    }
    

    @GetMapping("/receta/{recetaId}/link")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> obtenerLinkCompartir(@PathVariable Long recetaId) {
        String link = compartirService.generarLinkCompartir(recetaId, baseUrl);
        
        Map<String, String> response = new HashMap<>();
        response.put("link", link);
        response.put("facebook", "https://www.facebook.com/sharer/sharer.php?u=" + link);
        response.put("twitter", "https://twitter.com/intent/tweet?url=" + link);
        response.put("whatsapp", "https://wa.me/?text=" + link);
        
        return ResponseEntity.ok(response);
    }
}

