package com.recetas.recetas.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;


public class RecetaRequest {
    
    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;
    
    @NotBlank(message = "Los ingredientes son obligatorios")
    private String ingredientes;
    
    @NotBlank(message = "Las instrucciones son obligatorias")
    private String instrucciones;
    
    private String tipoCocina;
    
    private String paisOrigen;
    
    @NotNull(message = "El tiempo de cocción es obligatorio")
    @Positive(message = "El tiempo de cocción debe ser un número positivo")
    private Integer tiempoCoccion;
    
    private String dificultad;
    
    public RecetaRequest() {
    }
    
    public String getNombre() {
        return nombre;
    }
    
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    
    public String getIngredientes() {
        return ingredientes;
    }
    
    public void setIngredientes(String ingredientes) {
        this.ingredientes = ingredientes;
    }
    
    public String getInstrucciones() {
        return instrucciones;
    }
    
    public void setInstrucciones(String instrucciones) {
        this.instrucciones = instrucciones;
    }
    
    public String getTipoCocina() {
        return tipoCocina;
    }
    
    public void setTipoCocina(String tipoCocina) {
        this.tipoCocina = tipoCocina;
    }
    
    public String getPaisOrigen() {
        return paisOrigen;
    }
    
    public void setPaisOrigen(String paisOrigen) {
        this.paisOrigen = paisOrigen;
    }
    
    public Integer getTiempoCoccion() {
        return tiempoCoccion;
    }
    
    public void setTiempoCoccion(Integer tiempoCoccion) {
        this.tiempoCoccion = tiempoCoccion;
    }
    
    public String getDificultad() {
        return dificultad;
    }
    
    public void setDificultad(String dificultad) {
        this.dificultad = dificultad;
    }
}

