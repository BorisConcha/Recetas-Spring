package com.recetas.recetas.service;

import com.recetas.recetas.model.DetallePedido;
import com.recetas.recetas.model.Pedido;
import com.recetas.recetas.model.Producto;
import com.recetas.recetas.model.Usuario;
import com.recetas.recetas.repository.DetallePedidoRepository;
import com.recetas.recetas.repository.PedidoRepository;
import com.recetas.recetas.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@Transactional
public class PedidoService {
    
    @Autowired
    private PedidoRepository pedidoRepository;
    
    @Autowired
    private DetallePedidoRepository detallePedidoRepository;
    
    @Autowired
    private ProductoRepository productoRepository;
    
    public List<Pedido> obtenerTodosLosPedidos() {
        return pedidoRepository.findAll();
    }
    
    public List<Pedido> obtenerPedidosPorUsuario(Usuario usuario) {
        return pedidoRepository.findByUsuarioOrderByFechaPedidoDesc(usuario);
    }
    
    public List<Pedido> obtenerPedidosPorEstado(String estado) {
        return pedidoRepository.findByEstado(estado);
    }
    
    public Optional<Pedido> obtenerPedidoPorId(Long id) {
        return pedidoRepository.findById(id);
    }
    
    public Pedido crearPedido(Pedido pedido, Set<DetallePedido> detalles) {
        BigDecimal total = BigDecimal.ZERO;
        for (DetallePedido detalle : detalles) {
            Optional<Producto> productoOpt = productoRepository.findById(detalle.getProducto().getId());
            if (productoOpt.isPresent()) {
                Producto producto = productoOpt.get();
                if (!producto.isActivo() || producto.getStock() < detalle.getCantidad()) {
                    throw new RuntimeException("Producto no disponible o stock insuficiente: " + producto.getNombre());
                }
                detalle.setPrecioUnitario(producto.getPrecio());
                detalle.setSubtotal(producto.getPrecio().multiply(BigDecimal.valueOf(detalle.getCantidad())));
                detalle.setPedido(pedido);
                total = total.add(detalle.getSubtotal());
                
                producto.setStock(producto.getStock() - detalle.getCantidad());
                productoRepository.save(producto);
            }
        }
        pedido.setTotal(total);
        
        Pedido pedidoGuardado = pedidoRepository.save(pedido);
        
        for (DetallePedido detalle : detalles) {
            detallePedidoRepository.save(detalle);
        }
        
        return pedidoGuardado;
    }
    
    public Pedido actualizarEstadoPedido(Long id, String nuevoEstado) {
        Optional<Pedido> pedidoOpt = pedidoRepository.findById(id);
        if (pedidoOpt.isPresent()) {
            Pedido pedido = pedidoOpt.get();
            pedido.setEstado(nuevoEstado);
            return pedidoRepository.save(pedido);
        }
        throw new RuntimeException("Pedido no encontrado");
    }
    
    public void cancelarPedido(Long id) {
        Optional<Pedido> pedidoOpt = pedidoRepository.findById(id);
        if (pedidoOpt.isPresent()) {
            Pedido pedido = pedidoOpt.get();
            if (!"CANCELADO".equals(pedido.getEstado()) && !"ENTREGADO".equals(pedido.getEstado())) {
                List<DetallePedido> detalles = detallePedidoRepository.findByPedido(pedido);
                for (DetallePedido detalle : detalles) {
                    Optional<Producto> productoOpt = productoRepository.findById(detalle.getProducto().getId());
                    if (productoOpt.isPresent()) {
                        Producto producto = productoOpt.get();
                        producto.setStock(producto.getStock() + detalle.getCantidad());
                        productoRepository.save(producto);
                    }
                }
                pedido.setEstado("CANCELADO");
                pedidoRepository.save(pedido);
            }
        }
    }
}

