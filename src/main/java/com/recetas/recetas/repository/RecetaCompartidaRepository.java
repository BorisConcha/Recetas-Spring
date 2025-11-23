package com.recetas.recetas.repository;

import com.recetas.recetas.model.RecetaCompartida;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface RecetaCompartidaRepository extends JpaRepository<RecetaCompartida, Long> {
    List<RecetaCompartida> findByRecetaIdOrderByFechaCompartidoDesc(Long recetaId);
    long countByRecetaId(Long recetaId);
}

