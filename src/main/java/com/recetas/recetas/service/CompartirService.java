package com.recetas.recetas.service;

import com.recetas.recetas.model.Receta;
import com.recetas.recetas.model.RecetaCompartida;
import com.recetas.recetas.model.Usuario;
import com.recetas.recetas.repository.RecetaCompartidaRepository;
import com.recetas.recetas.repository.RecetaRepository;
import com.recetas.recetas.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CompartirService {
    
    @Autowired
    private RecetaCompartidaRepository recetaCompartidaRepository;
    
    @Autowired
    private RecetaRepository recetaRepository;
    
    @Autowired
    private UsuarioRepository usuarioRepository;
    
    @Transactional
    public RecetaCompartida registrarCompartido(Long recetaId, Long usuarioId, String plataforma) {
        Receta receta = recetaRepository.findById(recetaId)
                .orElseThrow(() -> new RuntimeException("Receta no encontrada con ID: " + recetaId));
        
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + usuarioId));
        
        RecetaCompartida recetaCompartida = new RecetaCompartida();
        recetaCompartida.setReceta(receta);
        recetaCompartida.setUsuario(usuario);
        recetaCompartida.setPlataforma(plataforma);
        
        return recetaCompartidaRepository.save(recetaCompartida);
    }
    
    public long contarCompartidosPorReceta(Long recetaId) {
        return recetaCompartidaRepository.countByRecetaId(recetaId);
    }
    
    public String generarLinkCompartir(Long recetaId, String baseUrl) {
        return baseUrl + "/recetas/" + recetaId;
    }
}

