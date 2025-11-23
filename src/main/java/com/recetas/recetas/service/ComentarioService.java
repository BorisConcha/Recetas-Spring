package com.recetas.recetas.service;

import com.recetas.recetas.dto.ComentarioResponse;
import com.recetas.recetas.model.Comentario;
import com.recetas.recetas.model.Receta;
import com.recetas.recetas.model.Usuario;
import com.recetas.recetas.repository.ComentarioRepository;
import com.recetas.recetas.repository.RecetaRepository;
import com.recetas.recetas.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ComentarioService {
    
    @Autowired
    private ComentarioRepository comentarioRepository;
    
    @Autowired
    private RecetaRepository recetaRepository;
    
    @Autowired
    private UsuarioRepository usuarioRepository;
    
    @Transactional
    public Comentario crearComentario(Long recetaId, Long usuarioId, String comentarioTexto) {
        Receta receta = recetaRepository.findById(recetaId)
                .orElseThrow(() -> new RuntimeException("Receta no encontrada con ID: " + recetaId));
        
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + usuarioId));
        
        Comentario comentario = new Comentario();
        comentario.setReceta(receta);
        comentario.setUsuario(usuario);
        comentario.setComentario(comentarioTexto);
        
        return comentarioRepository.save(comentario);
    }
    
    public List<ComentarioResponse> obtenerComentariosPorReceta(Long recetaId) {
        List<Comentario> comentarios = comentarioRepository.findByRecetaIdOrderByFechaCreacionDesc(recetaId);
        
        return comentarios.stream()
                .map(c -> new ComentarioResponse(
                        c.getId(),
                        c.getReceta().getId(),
                        c.getUsuario().getNombreCompleto(),
                        c.getComentario(),
                        c.getFechaCreacion()
                ))
                .collect(Collectors.toList());
    }
    
    @Transactional
    public void eliminarComentario(Long comentarioId, Long usuarioId) {
        Comentario comentario = comentarioRepository.findByIdAndUsuarioId(comentarioId, usuarioId)
                .orElseThrow(() -> new RuntimeException("Comentario no encontrado o no tienes permisos para eliminarlo"));
        
        comentarioRepository.delete(comentario);
    }
    
    public long contarComentariosPorReceta(Long recetaId) {
        return comentarioRepository.countByRecetaId(recetaId);
    }
}

