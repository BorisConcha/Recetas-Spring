package com.recetas.recetas.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;


public class CompartirRequest {
    
    @NotNull(message = "El ID de la receta es obligatorio")
    private Long recetaId;
    
    @NotBlank(message = "La plataforma es obligatoria")
    private String plataforma;
    
    public CompartirRequest() {
    }
    
    public Long getRecetaId() {
        return recetaId;
    }
    
    public void setRecetaId(Long recetaId) {
        this.recetaId = recetaId;
    }
    
    public String getPlataforma() {
        return plataforma;
    }
    
    public void setPlataforma(String plataforma) {
        this.plataforma = plataforma;
    }
}

