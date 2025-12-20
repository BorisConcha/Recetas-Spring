package com.recetas.recetas.repository;

import com.recetas.recetas.model.DetallePedido;
import com.recetas.recetas.model.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DetallePedidoRepository extends JpaRepository<DetallePedido, Long> {
    
    List<DetallePedido> findByPedido(Pedido pedido);
}

