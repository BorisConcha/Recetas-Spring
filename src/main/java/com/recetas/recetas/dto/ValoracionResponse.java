package com.recetas.recetas.dto;


public class ValoracionResponse {
    
    private Double promedio;
    private Long totalValoraciones;
    private Integer miValoracion;
    
    public ValoracionResponse() {
    }
    
    public ValoracionResponse(Double promedio, Long totalValoraciones, Integer miValoracion) {
        this.promedio = promedio;
        this.totalValoraciones = totalValoraciones;
        this.miValoracion = miValoracion;
    }
    
    public Double getPromedio() {
        return promedio;
    }
    
    public void setPromedio(Double promedio) {
        this.promedio = promedio;
    }
    
    public Long getTotalValoraciones() {
        return totalValoraciones;
    }
    
    public void setTotalValoraciones(Long totalValoraciones) {
        this.totalValoraciones = totalValoraciones;
    }
    
    public Integer getMiValoracion() {
        return miValoracion;
    }
    
    public void setMiValoracion(Integer miValoracion) {
        this.miValoracion = miValoracion;
    }
}

