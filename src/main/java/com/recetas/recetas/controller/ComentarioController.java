package com.recetas.recetas.controller;

import com.recetas.recetas.dto.ComentarioRequest;
import com.recetas.recetas.dto.ComentarioResponse;
import com.recetas.recetas.model.Usuario;
import com.recetas.recetas.repository.UsuarioRepository;
import com.recetas.recetas.service.ComentarioService;
import com.recetas.recetas.util.SecurityUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/comentarios")
public class ComentarioController {
    
    @Autowired
    private ComentarioService comentarioService;
    
    @Autowired
    private UsuarioRepository usuarioRepository;
    

    @PostMapping
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<?> crearComentario(@Valid @RequestBody ComentarioRequest request) {
        try {
            String username = SecurityUtil.getCurrentUsername();
            if (username == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Usuario no autenticado");
            }
            
            Usuario usuario = usuarioRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
            
            comentarioService.crearComentario(
                    request.getRecetaId(),
                    usuario.getId(),
                    request.getComentario()
            );
            
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body("Comentario creado exitosamente");
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error al crear comentario: " + e.getMessage());
        }
    }
    

    @GetMapping("/receta/{recetaId}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<List<ComentarioResponse>> obtenerComentarios(@PathVariable Long recetaId) {
        List<ComentarioResponse> comentarios = comentarioService.obtenerComentariosPorReceta(recetaId);
        return ResponseEntity.ok(comentarios);
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<?> eliminarComentario(@PathVariable Long id) {
        try {
            String username = SecurityUtil.getCurrentUsername();
            if (username == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Usuario no autenticado");
            }
            
            Usuario usuario = usuarioRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
            
            comentarioService.eliminarComentario(id, usuario.getId());
            
            return ResponseEntity.ok("Comentario eliminado exitosamente");
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error al eliminar comentario: " + e.getMessage());
        }
    }
}

