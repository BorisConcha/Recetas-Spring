package com.recetas.recetas.repository;

import com.recetas.recetas.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {
    
    List<Producto> findByActivo(Integer activo);
    
    Optional<Producto> findByIdAndActivo(Long id, Integer activo);
    
    List<Producto> findByCategoriaAndActivo(String categoria, Integer activo);
    
    @Query("SELECT p FROM Producto p WHERE p.activo = 1 ORDER BY p.fechaCreacion DESC")
    List<Producto> findProductosActivosRecientes();
}

