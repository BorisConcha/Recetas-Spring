package com.recetas.recetas.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;


@Entity
@Table(name = "receta_fotos")
@Data
public class RecetaFoto {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receta_id", nullable = false)
    private Receta receta;
    
    @Column(name = "url_foto", nullable = false, length = 500)
    private String urlFoto;
    
    @Column(name = "nombre_archivo", length = 255)
    private String nombreArchivo;
    
    @Column(name = "tipo_archivo", length = 50)
    private String tipoArchivo;
    
    @Column(name = "tamaño_archivo")
    private Long tamañoArchivo;
    
    @Column(name = "fecha_subida", nullable = false)
    private LocalDateTime fechaSubida = LocalDateTime.now();
    
    @Column(name = "es_principal")
    private Boolean esPrincipal = false;
}

