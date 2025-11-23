package com.recetas.recetas.repository;

import com.recetas.recetas.model.Valoracion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface ValoracionRepository extends JpaRepository<Valoracion, Long> {
    Optional<Valoracion> findByRecetaIdAndUsuarioId(Long recetaId, Long usuarioId);
    
    @Query("SELECT AVG(v.puntuacion) FROM Valoracion v WHERE v.receta.id = :recetaId")
    Double calcularPromedioPorRecetaId(@Param("recetaId") Long recetaId);
    
    @Query("SELECT COUNT(v) FROM Valoracion v WHERE v.receta.id = :recetaId")
    Long contarPorRecetaId(@Param("recetaId") Long recetaId);
}

