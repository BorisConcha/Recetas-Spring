package com.recetas.recetas.dto;

import java.time.LocalDateTime;


public class ComentarioResponse {
    
    private Long id;
    private Long recetaId;
    private String usuarioNombre;
    private String comentario;
    private LocalDateTime fechaCreacion;
    
    public ComentarioResponse() {
    }
    
    public ComentarioResponse(Long id, Long recetaId, String usuarioNombre, String comentario, LocalDateTime fechaCreacion) {
        this.id = id;
        this.recetaId = recetaId;
        this.usuarioNombre = usuarioNombre;
        this.comentario = comentario;
        this.fechaCreacion = fechaCreacion;
    }
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getRecetaId() {
        return recetaId;
    }
    
    public void setRecetaId(Long recetaId) {
        this.recetaId = recetaId;
    }
    
    public String getUsuarioNombre() {
        return usuarioNombre;
    }
    
    public void setUsuarioNombre(String usuarioNombre) {
        this.usuarioNombre = usuarioNombre;
    }
    
    public String getComentario() {
        return comentario;
    }
    
    public void setComentario(String comentario) {
        this.comentario = comentario;
    }
    
    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }
    
    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }
}

