package com.recetas.recetas.controller;

import com.recetas.recetas.dto.PedidoRequest;
import com.recetas.recetas.model.DetallePedido;
import com.recetas.recetas.model.Pedido;
import com.recetas.recetas.model.Producto;
import com.recetas.recetas.model.Usuario;
import com.recetas.recetas.repository.UsuarioRepository;
import com.recetas.recetas.service.PedidoService;
import com.recetas.recetas.service.ProductoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {
    
    @Autowired
    private PedidoService pedidoService;
    
    @Autowired
    private ProductoService productoService;
    
    @Autowired
    private UsuarioRepository usuarioRepository;
    
    @GetMapping
    public ResponseEntity<List<Pedido>> obtenerMisPedidos() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            
            var usuarioOpt = usuarioRepository.findByUsername(username);
            if (usuarioOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(null);
            }
            
            List<Pedido> pedidos = pedidoService.obtenerPedidosPorUsuario(usuarioOpt.get());
            return ResponseEntity.ok(pedidos);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(null);
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerPedidoPorId(@PathVariable Long id) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            
            var usuarioOpt = usuarioRepository.findByUsername(username);
            if (usuarioOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Usuario no encontrado");
            }
            
            Optional<Pedido> pedidoOpt = pedidoService.obtenerPedidoPorId(id);
            if (pedidoOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Pedido no encontrado");
            }
            
            Pedido pedido = pedidoOpt.get();

            if (!pedido.getUsuario().getUsername().equals(username)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("No tiene permiso para ver este pedido");
            }
            
            return ResponseEntity.ok(pedido);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error al obtener el pedido: " + e.getMessage());
        }
    }
    
    @PostMapping
    public ResponseEntity<?> crearPedido(@Valid @RequestBody PedidoRequest request) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            
            var usuarioOpt = usuarioRepository.findByUsername(username);
            if (usuarioOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Usuario no encontrado");
            }
            
            Usuario usuario = usuarioOpt.get();
            
            Pedido pedido = new Pedido();
            pedido.setUsuario(usuario);
            pedido.setDireccionEntrega(request.getDireccionEntrega());
            pedido.setTelefonoContacto(request.getTelefonoContacto());
            pedido.setEstado("PENDIENTE");
            
            Set<DetallePedido> detalles = new HashSet<>();
            for (PedidoRequest.DetallePedidoRequest detalleReq : request.getDetalles()) {
                Optional<Producto> productoOpt = productoService.obtenerProductoActivoPorId(detalleReq.getProductoId());
                if (productoOpt.isEmpty()) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body("Producto no encontrado: " + detalleReq.getProductoId());
                }
                
                Producto producto = productoOpt.get();
                if (producto.getStock() < detalleReq.getCantidad()) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body("Stock insuficiente para el producto: " + producto.getNombre());
                }
                
                DetallePedido detalle = new DetallePedido();
                detalle.setProducto(producto);
                detalle.setCantidad(detalleReq.getCantidad());
                detalles.add(detalle);
            }
            
            Pedido pedidoGuardado = pedidoService.crearPedido(pedido, detalles);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(pedidoGuardado);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error al crear el pedido: " + e.getMessage());
        }
    }
    
    @PutMapping("/{id}/estado")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> actualizarEstadoPedido(@PathVariable Long id, 
                                                     @RequestBody Map<String, String> request) {
        try {
            String nuevoEstado = request.get("estado");
            if (nuevoEstado == null || nuevoEstado.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("El estado es obligatorio");
            }
            
            Pedido pedido = pedidoService.actualizarEstadoPedido(id, nuevoEstado);
            return ResponseEntity.ok(pedido);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error al actualizar el estado del pedido: " + e.getMessage());
        }
    }
    
    @PutMapping("/{id}/cancelar")
    public ResponseEntity<?> cancelarPedido(@PathVariable Long id) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            
            Optional<Pedido> pedidoOpt = pedidoService.obtenerPedidoPorId(id);
            if (pedidoOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Pedido no encontrado");
            }
            
            Pedido pedido = pedidoOpt.get();
            
            boolean isAdmin = authentication.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
            
            if (!pedido.getUsuario().getUsername().equals(username) && !isAdmin) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("No tiene permiso para cancelar este pedido");
            }
            
            pedidoService.cancelarPedido(id);
            return ResponseEntity.ok(Map.of("mensaje", "Pedido cancelado correctamente"));
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error al cancelar el pedido: " + e.getMessage());
        }
    }
    
    @GetMapping("/admin/todos")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Pedido>> obtenerTodosLosPedidos() {
        List<Pedido> pedidos = pedidoService.obtenerTodosLosPedidos();
        return ResponseEntity.ok(pedidos);
    }
    
    @GetMapping("/admin/estado/{estado}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Pedido>> obtenerPedidosPorEstado(@PathVariable String estado) {
        List<Pedido> pedidos = pedidoService.obtenerPedidosPorEstado(estado);
        return ResponseEntity.ok(pedidos);
    }
}

