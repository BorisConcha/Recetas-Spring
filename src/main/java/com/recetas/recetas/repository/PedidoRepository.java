package com.recetas.recetas.repository;

import com.recetas.recetas.model.Pedido;
import com.recetas.recetas.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {
    
    List<Pedido> findByUsuario(Usuario usuario);
    
    List<Pedido> findByUsuarioOrderByFechaPedidoDesc(Usuario usuario);
    
    @Query("SELECT p FROM Pedido p WHERE p.estado = :estado ORDER BY p.fechaPedido DESC")
    List<Pedido> findByEstado(String estado);
}

