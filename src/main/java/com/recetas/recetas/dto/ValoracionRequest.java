package com.recetas.recetas.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;


public class ValoracionRequest {
    
    @NotNull(message = "El ID de la receta es obligatorio")
    private Long recetaId;
    
    @NotNull(message = "La puntuación es obligatoria")
    @Min(value = 1, message = "La puntuación debe ser al menos 1")
    @Max(value = 5, message = "La puntuación debe ser máximo 5")
    private Integer puntuacion;
    
    public ValoracionRequest() {
    }
    
    public Long getRecetaId() {
        return recetaId;
    }
    
    public void setRecetaId(Long recetaId) {
        this.recetaId = recetaId;
    }
    
    public Integer getPuntuacion() {
        return puntuacion;
    }
    
    public void setPuntuacion(Integer puntuacion) {
        this.puntuacion = puntuacion;
    }
}

