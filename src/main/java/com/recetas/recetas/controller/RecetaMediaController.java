package com.recetas.recetas.controller;

import com.recetas.recetas.model.RecetaFoto;
import com.recetas.recetas.model.RecetaVideo;
import com.recetas.recetas.service.RecetaFotoService;
import com.recetas.recetas.service.RecetaVideoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/api/recetas")
public class RecetaMediaController {
    
    @Autowired
    private RecetaFotoService recetaFotoService;
    
    @Autowired
    private RecetaVideoService recetaVideoService;
    

    @PostMapping("/{id}/fotos")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<?> subirFoto(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file) {
        try {
            RecetaFoto foto = recetaFotoService.subirFoto(id, file);
            return ResponseEntity.status(HttpStatus.CREATED).body(foto);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error al subir foto: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error: " + e.getMessage());
        }
    }
    

    @GetMapping("/{id}/fotos")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<List<RecetaFoto>> obtenerFotos(@PathVariable Long id) {
        List<RecetaFoto> fotos = recetaFotoService.obtenerFotosPorReceta(id);
        return ResponseEntity.ok(fotos);
    }
    

    @DeleteMapping("/{id}/fotos/{fotoId}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<?> eliminarFoto(@PathVariable Long id, @PathVariable Long fotoId) {
        try {
            recetaFotoService.eliminarFoto(fotoId);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Foto eliminada exitosamente");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error al eliminar foto: " + e.getMessage());
        }
    }
    

    @PostMapping("/{id}/videos")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<?> subirVideo(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file) {
        try {
            RecetaVideo video = recetaVideoService.subirVideo(id, file);
            return ResponseEntity.status(HttpStatus.CREATED).body(video);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error al subir video: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error: " + e.getMessage());
        }
    }
    

    @GetMapping("/{id}/videos")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<List<RecetaVideo>> obtenerVideos(@PathVariable Long id) {
        List<RecetaVideo> videos = recetaVideoService.obtenerVideosPorReceta(id);
        return ResponseEntity.ok(videos);
    }
    

    @DeleteMapping("/{id}/videos/{videoId}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<?> eliminarVideo(@PathVariable Long id, @PathVariable Long videoId) {
        try {
            recetaVideoService.eliminarVideo(videoId);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Video eliminado exitosamente");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error al eliminar video: " + e.getMessage());
        }
    }
}

