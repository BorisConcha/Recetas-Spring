package com.recetas.recetas.controller;

import com.recetas.recetas.model.Pedido;
import com.recetas.recetas.repository.PedidoRepository;
import com.recetas.recetas.repository.UsuarioRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/pagos")
public class PagoController {
    
    @Autowired
    private PedidoRepository pedidoRepository;
    
    @Autowired
    private UsuarioRepository usuarioRepository;
    
    @PostMapping("/simular")
    public ResponseEntity<?> simularPago(@Valid @RequestBody Map<String, Long> request) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            
            var usuarioOpt = usuarioRepository.findByUsername(username);
            if (usuarioOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Usuario no encontrado");
            }
            
            Long pedidoId = request.get("pedidoId");
            if (pedidoId == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("El ID del pedido es obligatorio");
            }
            
            var pedidoOpt = pedidoRepository.findById(pedidoId);
            if (pedidoOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Pedido no encontrado");
            }
            
            Pedido pedido = pedidoOpt.get();
            
            if (!pedido.getUsuario().getUsername().equals(username)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("No tiene permiso para pagar este pedido");
            }
            
            if (!"PENDIENTE".equals(pedido.getEstado())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("El pedido no está en estado PENDIENTE. Estado actual: " + pedido.getEstado());
            }
            
            pedido.setEstado("CONFIRMADO");
            pedidoRepository.save(pedido);
            
            Map<String, Object> response = new HashMap<>();
            response.put("mensaje", "Pago procesado exitosamente");
            response.put("pedidoId", pedido.getId());
            response.put("monto", pedido.getTotal());
            response.put("estado", pedido.getEstado());
            response.put("numeroTransaccion", "SIM-" + System.currentTimeMillis());
            response.put("fechaPago", java.time.LocalDateTime.now().toString());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error al procesar el pago: " + e.getMessage());
        }
    }
    
    @GetMapping("/pedido/{pedidoId}")
    public ResponseEntity<?> obtenerEstadoPago(@PathVariable Long pedidoId) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            
            var pedidoOpt = pedidoRepository.findById(pedidoId);
            if (pedidoOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Pedido no encontrado");
            }
            
            Pedido pedido = pedidoOpt.get();
            
            if (!pedido.getUsuario().getUsername().equals(username)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("No tiene permiso para ver este pedido");
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("pedidoId", pedido.getId());
            response.put("estado", pedido.getEstado());
            response.put("monto", pedido.getTotal());
            response.put("pagado", !"PENDIENTE".equals(pedido.getEstado()));
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error al obtener el estado del pago: " + e.getMessage());
        }
    }
}

