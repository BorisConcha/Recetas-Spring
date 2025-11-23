package com.recetas.recetas.repository;

import com.recetas.recetas.model.Comentario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ComentarioRepository extends JpaRepository<Comentario, Long> {
    List<Comentario> findByRecetaIdOrderByFechaCreacionDesc(Long recetaId);
    Optional<Comentario> findByIdAndUsuarioId(Long id, Long usuarioId);
    long countByRecetaId(Long recetaId);
}

