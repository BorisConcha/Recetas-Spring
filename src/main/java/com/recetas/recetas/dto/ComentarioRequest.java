package com.recetas.recetas.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;


public class ComentarioRequest {
    
    @NotNull(message = "El ID de la receta es obligatorio")
    private Long recetaId;
    
    @NotBlank(message = "El comentario no puede estar vacío")
    private String comentario;
    
    public ComentarioRequest() {
    }
    
    public Long getRecetaId() {
        return recetaId;
    }
    
    public void setRecetaId(Long recetaId) {
        this.recetaId = recetaId;
    }
    
    public String getComentario() {
        return comentario;
    }
    
    public void setComentario(String comentario) {
        this.comentario = comentario;
    }
}

