package com.recetas.recetas.repository;

import com.recetas.recetas.model.Receta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface RecetaRepository extends JpaRepository<Receta, Long>{

    @Query("SELECT r FROM Receta r ORDER BY r.fechaCreacion DESC")
    List<Receta> findTop6ByOrderByFechaCreacionDesc();
    
    @Query("SELECT r FROM Receta r ORDER BY r.popularidad DESC")
    List<Receta> findTop6ByOrderByPopularidadDesc();
    
    @Query("SELECT r FROM Receta r WHERE LOWER(r.nombre) LIKE LOWER(CONCAT('%', :nombre, '%'))")
    List<Receta> findByNombreContainingIgnoreCase(@Param("nombre") String nombre);
    
    @Query("SELECT r FROM Receta r WHERE LOWER(r.tipoCocina) LIKE LOWER(CONCAT('%', :tipoCocina, '%'))")
    List<Receta> findByTipoCocinaContainingIgnoreCase(@Param("tipoCocina") String tipoCocina);
    
    @Query("SELECT r FROM Receta r WHERE LOWER(r.paisOrigen) LIKE LOWER(CONCAT('%', :paisOrigen, '%'))")
    List<Receta> findByPaisOrigenContainingIgnoreCase(@Param("paisOrigen") String paisOrigen);
    
    @Query("SELECT r FROM Receta r WHERE LOWER(r.dificultad) LIKE LOWER(CONCAT('%', :dificultad, '%'))")
    List<Receta> findByDificultadContainingIgnoreCase(@Param("dificultad") String dificultad);
    
    @Query(value = "SELECT * FROM recetas r WHERE DBMS_LOB.INSTR(LOWER(r.ingredientes), LOWER(:ingrediente)) > 0", 
           nativeQuery = true)
    List<Receta> findByIngredientesContainingIgnoreCase(@Param("ingrediente") String ingrediente);
}
