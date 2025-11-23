package com.recetas.recetas.controller;

import com.recetas.recetas.dto.ValoracionRequest;
import com.recetas.recetas.dto.ValoracionResponse;
import com.recetas.recetas.model.Usuario;
import com.recetas.recetas.repository.UsuarioRepository;
import com.recetas.recetas.service.ValoracionService;
import com.recetas.recetas.util.SecurityUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/valoraciones")
public class ValoracionController {
    
    @Autowired
    private ValoracionService valoracionService;
    
    @Autowired
    private UsuarioRepository usuarioRepository;
    

    @PostMapping
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<?> crearOActualizarValoracion(@Valid @RequestBody ValoracionRequest request) {
        try {
            String username = SecurityUtil.getCurrentUsername();
            if (username == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Usuario no autenticado");
            }
            
            Usuario usuario = usuarioRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
            
            valoracionService.crearOActualizarValoracion(
                    request.getRecetaId(),
                    usuario.getId(),
                    request.getPuntuacion()
            );
            
            return ResponseEntity.ok("Valoración guardada exitosamente");
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error al guardar valoración: " + e.getMessage());
        }
    }
    

    @GetMapping("/receta/{recetaId}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ValoracionResponse> obtenerValoraciones(@PathVariable Long recetaId) {
        try {
            String username = SecurityUtil.getCurrentUsername();
            Long usuarioId = null;
            
            if (username != null) {
                Usuario usuario = usuarioRepository.findByUsername(username).orElse(null);
                if (usuario != null) {
                    usuarioId = usuario.getId();
                }
            }
            
            ValoracionResponse response = valoracionService.obtenerValoracionesPorReceta(recetaId, usuarioId);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
}

