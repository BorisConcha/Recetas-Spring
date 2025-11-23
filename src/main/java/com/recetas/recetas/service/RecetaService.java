package com.recetas.recetas.service;

import com.recetas.recetas.model.Receta;
import com.recetas.recetas.repository.RecetaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class RecetaService {
    
    @Autowired
    private RecetaRepository recetaRepository;

    public List<Receta> obtenerRecetasRecientes() {
        return recetaRepository.findTop6ByOrderByFechaCreacionDesc();
    }

    public List<Receta> obtenerRecetasPopulares() {
        return recetaRepository.findTop6ByOrderByPopularidadDesc();
    }

    public List<Receta> buscarPorNombre(String nombre) {
        return recetaRepository.findByNombreContainingIgnoreCase(nombre);
    }

    public List<Receta> buscarPorTipoCocina(String tipoCocina) {
        return recetaRepository.findByTipoCocinaContainingIgnoreCase(tipoCocina);
    }

    public List<Receta> buscarPorPaisOrigen(String paisOrigen) {
        return recetaRepository.findByPaisOrigenContainingIgnoreCase(paisOrigen);
    }

    public List<Receta> buscarPorDificultad(String dificultad) {
        return recetaRepository.findByDificultadContainingIgnoreCase(dificultad);
    }

    public List<Receta> buscarPorIngrediente(String ingrediente) {
        return recetaRepository.findByIngredientesContainingIgnoreCase(ingrediente);
    }

    public Optional<Receta> obtenerRecetaPorId(Long id) {
        return recetaRepository.findById(id);
    }

    public List<Receta> obtenerTodasLasRecetas() {
        return recetaRepository.findAll();
    }

    public Receta guardarReceta(Receta receta) {
        return recetaRepository.save(receta);
    }
}
