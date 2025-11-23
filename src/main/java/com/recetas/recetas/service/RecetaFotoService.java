package com.recetas.recetas.service;

import com.recetas.recetas.model.Receta;
import com.recetas.recetas.model.RecetaFoto;
import com.recetas.recetas.repository.RecetaFotoRepository;
import com.recetas.recetas.repository.RecetaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
public class RecetaFotoService {
    
    @Autowired
    private RecetaFotoRepository recetaFotoRepository;
    
    @Autowired
    private RecetaRepository recetaRepository;
    
    @Autowired
    private ArchivoService archivoService;
    
    @Transactional
    public RecetaFoto subirFoto(Long recetaId, MultipartFile file) throws IOException {
        Receta receta = recetaRepository.findById(recetaId)
                .orElseThrow(() -> new RuntimeException("Receta no encontrada con ID: " + recetaId));
        
        String urlFoto = archivoService.guardarImagen(file);
        
        RecetaFoto foto = new RecetaFoto();
        foto.setReceta(receta);
        foto.setUrlFoto(urlFoto);
        foto.setNombreArchivo(file.getOriginalFilename());
        foto.setTipoArchivo(file.getContentType());
        foto.setTamañoArchivo(file.getSize());
        
        List<RecetaFoto> fotosExistentes = recetaFotoRepository.findByRecetaIdOrderByFechaSubidaDesc(recetaId);
        if (fotosExistentes.isEmpty()) {
            foto.setEsPrincipal(true);
        }
        
        return recetaFotoRepository.save(foto);
    }
    
    public List<RecetaFoto> obtenerFotosPorReceta(Long recetaId) {
        return recetaFotoRepository.findByRecetaIdOrderByFechaSubidaDesc(recetaId);
    }
    
    @Transactional
    public void eliminarFoto(Long fotoId) {
        recetaFotoRepository.deleteById(fotoId);
    }
    
    @Transactional
    public void marcarComoPrincipal(Long fotoId, Long recetaId) {
        List<RecetaFoto> fotos = recetaFotoRepository.findByRecetaIdOrderByFechaSubidaDesc(recetaId);
        fotos.forEach(f -> f.setEsPrincipal(false));
        recetaFotoRepository.saveAll(fotos);
        
        Optional<RecetaFoto> foto = recetaFotoRepository.findById(fotoId);
        if (foto.isPresent()) {
            foto.get().setEsPrincipal(true);
            recetaFotoRepository.save(foto.get());
        }
    }
}

