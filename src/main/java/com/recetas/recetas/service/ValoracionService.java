package com.recetas.recetas.service;

import com.recetas.recetas.dto.ValoracionResponse;
import com.recetas.recetas.model.Receta;
import com.recetas.recetas.model.Usuario;
import com.recetas.recetas.model.Valoracion;
import com.recetas.recetas.repository.RecetaRepository;
import com.recetas.recetas.repository.UsuarioRepository;
import com.recetas.recetas.repository.ValoracionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class ValoracionService {
    
    @Autowired
    private ValoracionRepository valoracionRepository;
    
    @Autowired
    private RecetaRepository recetaRepository;
    
    @Autowired
    private UsuarioRepository usuarioRepository;
    
    @Transactional
    public Valoracion crearOActualizarValoracion(Long recetaId, Long usuarioId, Integer puntuacion) {
        Receta receta = recetaRepository.findById(recetaId)
                .orElseThrow(() -> new RuntimeException("Receta no encontrada con ID: " + recetaId));
        
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + usuarioId));
        
        Optional<Valoracion> valoracionExistente = valoracionRepository.findByRecetaIdAndUsuarioId(recetaId, usuarioId);
        
        if (valoracionExistente.isPresent()) {
            // Actualizar valoración existente
            Valoracion valoracion = valoracionExistente.get();
            valoracion.setPuntuacion(puntuacion);
            valoracion.setFechaActualizacion(LocalDateTime.now());
            return valoracionRepository.save(valoracion);
        } else {
            // Crear nueva valoración
            Valoracion valoracion = new Valoracion();
            valoracion.setReceta(receta);
            valoracion.setUsuario(usuario);
            valoracion.setPuntuacion(puntuacion);
            return valoracionRepository.save(valoracion);
        }
    }
    
    public ValoracionResponse obtenerValoracionesPorReceta(Long recetaId, Long usuarioId) {
        Double promedio = valoracionRepository.calcularPromedioPorRecetaId(recetaId);
        Long totalValoraciones = valoracionRepository.contarPorRecetaId(recetaId);
        
        Integer miValoracion = null;
        if (usuarioId != null) {
            Optional<Valoracion> miValoracionOpt = valoracionRepository.findByRecetaIdAndUsuarioId(recetaId, usuarioId);
            if (miValoracionOpt.isPresent()) {
                miValoracion = miValoracionOpt.get().getPuntuacion();
            }
        }
        
        return new ValoracionResponse(
                promedio != null ? promedio : 0.0,
                totalValoraciones != null ? totalValoraciones : 0L,
                miValoracion
        );
    }
}

