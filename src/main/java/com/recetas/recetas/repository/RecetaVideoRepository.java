package com.recetas.recetas.repository;

import com.recetas.recetas.model.RecetaVideo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface RecetaVideoRepository extends JpaRepository<RecetaVideo, Long> {
    List<RecetaVideo> findByRecetaIdOrderByFechaSubidaDesc(Long recetaId);
}

