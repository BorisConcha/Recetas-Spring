package com.recetas.recetas.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "anuncios")
@Data
public class Anuncio {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "anuncios_seq")
    @SequenceGenerator(name = "anuncios_seq", sequenceName = "anuncios_seq", allocationSize = 1)
    private Long id;
    
    @Column(name = "empresa", nullable = false, length = 200)
    private String empresa;
    
    @Column(name = "titulo", nullable = false, length = 200)
    private String titulo;
    
    @Column(name = "descripcion", columnDefinition = "CLOB")
    private String descripcion;
    
    @Column(name = "imagen_url", length = 500)
    private String imagenUrl;
    
    @Column(name = "url_destino", length = 500)
    private String urlDestino;
    
    @Column(name = "activo", nullable = false)
    private Integer activo = 1;
    
    public boolean isActivo() {
        return activo != null && activo == 1;
    }
    
    public void setActivoBoolean(boolean activo) {
        this.activo = activo ? 1 : 0;
    }
}
