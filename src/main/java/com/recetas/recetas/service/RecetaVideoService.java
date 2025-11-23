package com.recetas.recetas.service;

import com.recetas.recetas.model.Receta;
import com.recetas.recetas.model.RecetaVideo;
import com.recetas.recetas.repository.RecetaRepository;
import com.recetas.recetas.repository.RecetaVideoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public class RecetaVideoService {
    
    @Autowired
    private RecetaVideoRepository recetaVideoRepository;
    
    @Autowired
    private RecetaRepository recetaRepository;
    
    @Autowired
    private ArchivoService archivoService;
    
    @Transactional
    public RecetaVideo subirVideo(Long recetaId, MultipartFile file) throws IOException {
        Receta receta = recetaRepository.findById(recetaId)
                .orElseThrow(() -> new RuntimeException("Receta no encontrada con ID: " + recetaId));
        
        String urlVideo = archivoService.guardarVideo(file);
        
        RecetaVideo video = new RecetaVideo();
        video.setReceta(receta);
        video.setUrlVideo(urlVideo);
        video.setNombreArchivo(file.getOriginalFilename());
        video.setTipoArchivo(file.getContentType());
        video.setTamañoArchivo(file.getSize());
        
        return recetaVideoRepository.save(video);
    }
    
    public List<RecetaVideo> obtenerVideosPorReceta(Long recetaId) {
        return recetaVideoRepository.findByRecetaIdOrderByFechaSubidaDesc(recetaId);
    }
    
    @Transactional
    public void eliminarVideo(Long videoId) {
        recetaVideoRepository.deleteById(videoId);
    }
}

