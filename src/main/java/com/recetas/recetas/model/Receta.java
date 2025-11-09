package com.recetas.recetas.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "recetas")
@Data
public class Receta {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "nombre", nullable = false, length = 200)
    private String nombre;
    
    @Column(name = "tipo_cocina", length = 100)
    private String tipoCocina;
    
    @Column(name = "pais_origen", length = 100)
    private String paisOrigen;
    
    @Column(name = "dificultad", length = 50)
    private String dificultad;
    
    @Column(name = "tiempo_coccion")
    private Integer tiempoCoccion;
    
    @Lob
    @Column(name = "descripcion", columnDefinition = "CLOB")
    private String descripcion;
    
    @Lob
    @Column(name = "ingredientes", columnDefinition = "CLOB")
    private String ingredientes;
    
    @Lob
    @Column(name = "instrucciones", columnDefinition = "CLOB")
    private String instrucciones;
    
    @Column(name = "imagen_url", length = 500)
    private String imagenUrl;
    
    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion = LocalDateTime.now();
    
    @Column(name = "popularidad")
    private Integer popularidad = 0;
}
